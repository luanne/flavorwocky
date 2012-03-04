/*
Copyright (c) 2012, Luanne Misquitta
All rights reserved. See License.txt
 */
package com.herokuapp.flavorwocky

import groovyx.net.http.RESTClient
import net.sf.json.JSONNull
import net.sf.json.JSONArray
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.ContentType.JSON
import groovyx.net.http.HttpResponseException

class FlavorwockyController {

    /**
     * Ingredient autocomplete. After two characters are typed, search for ingredients that start with those two characters
     * @return Array of autosearch ingredients
     */

    def autosearch() {
        def results
        if (params.term) {
            render(contentType: "text/json") {
                results = array {
                    Ingredient.where { name =~ params.term+'%'}.list().each {
                        result id: it.id, name: it.name, label: it.name
                    }
                }
            }
        }
        else {
            render "Please provide a search term"
        }

    }

    /**
     * Check that the neo4j server is up and running
     * @return boolean, true if the server is up, false otherwise
     */
    def ping() {
        def serverEndpointOk = false
        //lets just count the Categories and if they are more than 0 then we assume the server is reachable and initialized.
        if (Category.count()>0) {
            serverEndpointOk = true
        }
        render serverEndpointOk
    }

    /**
     * Index action
     * @return Map of categories, affinities and the latest pairings
     */
    def index() {
        def categories = Category.list()
        //affinity values are hard-coded over here for simplicity
        [categories: categories,
                affinity: [0.35: 'Tried and tested', 0.45: 'Extremely good', 0.6: 'Good'],
                latest: getLatestPairings()
        ]
    }

    /**
     * Fetches the nodes references for ingredients if they exists, else creates them
     * @return List of the node references
     */
    private List fetchOrCreateNodes(String ingredient1, String ingredient2, String categoryNode1, String categoryNode2) {
        def storedIngredients = Ingredient.findAllByNameInList([ingredient1, ingredient2])

        def ingredient1Instance = storedIngredients.find {it.name==ingredient1}
        def ingredient2Instance = storedIngredients.find {it.name==ingredient2}
        if (!ingredient1Instance) {
            ingredient1Instance = new Ingredient(name: ingredient1, category: Category.get(categoryNode1)).save(failOnError: true)
        }

        if (!ingredient2Instance) {
            ingredient2Instance = new Ingredient(name: ingredient2, category: Category.get(categoryNode2)).save(failOnError: true)
        }

        return [ingredient1Instance, ingredient2Instance]
    }

    /**
     * Create flavor pair
     */
    def create() {
        if (!params.ingredient1 || !params.ingredient2 || !params.category1 || !params.category2 || !params.affinity) {
            render "Invalid parameter values"
            return
        }

        Ingredient.withTransaction {
            def pairedIngredients = fetchOrCreateNodes(params.ingredient1, params.ingredient2, params.category1, params.category2)
            createRelationship(pairedIngredients)
        }

        render "done"
    }

    /**
     * Creates a 'pairings' relationship between pairedIngredients
     * @param pairedIngredients List of 2 ingredients
     * @return true if a relationship was created, false if a relationship already exists
     */
    private boolean createRelationship(List pairedIngredients) {
        def res = Ingredient.cypherStatic("start n1=node({node1}), n2=node({node2}) match (n1)-[r:pairings]-(n2) return count(r)",
                                    [node1: pairedIngredients[0]?.id, node2: pairedIngredients[1]?.id]
                                )
        if (res.iterator().size() <=0 ) {
            //no relationship exists between these two ingredients, so lets create one
            pairedIngredients[0].addToPairings(pairedIngredients[1])
            updateRecentPairings(pairedIngredients)
            return true
        }

        return false
    }

    private void updateRecentPairings(List pairedIngredients) {
        def latestPairings = LatestPairing.list(sort: 'dateCreated', order: 'asc')
        if (latestPairings.size()>=5) {
            //always delete the oldest one
            latestPairings[0].delete()
        }

        new LatestPairing(nodeId: pairedIngredients[0].id, pairing: pairedIngredients[0].name + " and " + pairedIngredients[1].name)
            .save(failOneError: true)
    }

    /**
     * Return search results as a tree
     * @return JSON for tree visualization
     */
    def getSearchVisualizationAsTreeJson() {
        //TODO this is a really bad, inefficient way to build the JSON tree and is only good for demos.
        //TODO refactor for production or else be forever ashamed
        if (params.nodeId) {
            def nodeId = Integer.parseInt(params.nodeId)  //Integer.parseInt(params.nodeId.substring(params.nodeId.lastIndexOf('/')+1))
            List children = getChildren(1, nodeId, nodeId)
            def rootIngredient = Ingredient.get(nodeId)
            def finalStructure = ["name": rootIngredient.name, "catColor": rootIngredient.category.catColor, "wt": 0.5, "children": children]
            render finalStructure as grails.converters.JSON
        } else {
            render "error"
        }
    }

    /**
     * Get PAIRS_WITH ingredients up to a depth of 3
     * @param depth depth
     * @param nodeId start ingredient node
     * @param parentNodeId ancestor ingredient node
     * @return List of children
     */
    private List getChildren(int depth, int nodeId, int parentNodeId) {
        def childrenList = []

        if (depth > 3) {
            return null
        }

        def res = Ingredient.cypherStatic ("""start n=node({nodeId}), original=node({original})
                                              match (n)-[r:pairings]-(i)-[:category]->(cat)
                                              where not(i=original)
                                              return i.name as name ,cat.catColor as catColor ,ID(i) as idi""",
                                            [nodeId: nodeId, original: parentNodeId]
                                          )
        res.iterator().each {
            def child = ["name": it.name, "catColor": it.catColor, "wt": 0.5]
            child.put("children", getChildren(depth + 1, it.idi as int, nodeId))
            childrenList.add child
        }

        return childrenList
    }

    /**
     * Search results as a network
     * @return JSON for network visualization
     */
    def getSearchVisualizationAsNetworkJson() {
        if (params.nodeId) {
            def nodeId = Integer.parseInt(params.nodeId)
            def nodes = Ingredient.cypherStatic ("""start n=node({nodeId})
                                        match (n)-[p1:pairings]-(i1)-[p2?:pairings]-(i2)
                                                            -[p3?:pairings]-(i3)
                                                            -[p4?:pairings]-(i4)
                                                            -[p5?:pairings]-(i5),
                                              (n)-[:category]->(c0),
                                              (i1)-[?:category]->(c1),
                                              (i2)-[?:category]->(c2),
                                              (i3)-[?:category]->(c3),
                                              (i4)-[?:category]->(c4),
                                              (i5)-[?:category]->(c5)
                                        return n.name, c1.catColor?, p1.wt?, i1.name?,
                                                c2.catColor?, p2.wt?, i2.name?, c3.catColor?,
                                                p3.wt?, i3.name?, c4.catColor?, p4.wt?,i4.name?,
                                                c5.catColor?, p5.wt?, i5.name?""",
                                [nodeId: nodeId]
            )
            
            def nodeJsonArray = []
            def relationJsonArray = []
                    def nodeIndex = [:]
                    def relationshipIndex = [:]

                    def nodeCounter = 0
                    def srcIngredient = null

            nodes.iterator().each {
                        srcIngredient = null
                        for (i in 0..5) {
                            def ingredient = (i==0)?it['n.name']:it["i${i+1}.name"]
                            if (ingredient) {
                                if (!nodeIndex.containsKey(ingredient)) {
                                    nodeIndex.put(ingredient, nodeCounter)
                                    def catColor = "black"
                                    if (i != 0) {
                                        catColor = it["c${i+1}.catColor"]
                                    }
                                    def nodeProps = ["name": ingredient, "catColor": catColor]
                                    nodeJsonArray.add(nodeProps)
                                    nodeCounter++
                                }
                                if (srcIngredient != null) {
//                                    float distance = it["p${i+1}.wt"]
                                    float distance = 0.5
                                    mapRelation(srcIngredient, ingredient, relationshipIndex, relationJsonArray, nodeIndex, distance)
                                }
                                srcIngredient = ingredient
                            }
                        }
            }
            def finalStructure = ["nodes": nodeJsonArray, "links": relationJsonArray]
            render finalStructure as grails.converters.JSON
        } else {
            render "error"
        }
    }

    /**
     * Helper for constructing network JSON
     *
     */
    private void mapRelation(String src, String target, Map relationshipIndex, List relationJsonArray, Map nodeIndex, float distance) {

        if (relationshipIndex.containsKey(src)) {
            if (relationshipIndex.get(src).contains(target)) {
                return
            }
            else {
                relationshipIndex.get(src).add(target)
            }
        }
        else {
            def targetList = [target]
            relationshipIndex.put(src, targetList)
        }
        def relationProps = ["source": nodeIndex.get(src), "target": nodeIndex.get(target), "dist": distance]
        relationJsonArray.add(relationProps)

    }

    /**
     * Get flavor trios for an ingredient
     * @return JSON representation of trio list
     */
    def getFlavorTrios(Integer nodeId) {
        if (nodeId) {
            def nodes = Ingredient.cypherStatic ("""start n=node({nodeId})
                        match (n)-[r1:pairings]-(i1)-[r2:pairings]-(i2)-[r3:pairings]-(n)
                        return n.name as searchName, i1.name as ingred1,i2.name as ingred2, ID(r2) as relId, ID(i1) as ingred1Id
                        order by i1.name""",
                    [nodeId: nodeId]
            )
            def trioList = [] //list of pair
            def relationshipIds = [] //list of pairs_with relationship ids to check for bidirectional pairs
            nodes.iterator().each {
                if (!relationshipIds.contains(it.relId)) {
                    relationshipIds.add(it.relId)
                    def trioName = it.searchName + ", " + it.ingred1 + " and " + it.ingred2
                    def trio = ["trio": trioName, "nodeId": it.ingred1Id]
                    trioList.add(trio)
                }
            }
            render trioList as grails.converters.JSON

        }
        else {
            render "node id is missing"
        }
    }

    /**
     * Get latest pairings
     * @return JSONArray of latest pairings
     */
    private def getLatestPairings() {
        return LatestPairing.list(sort: 'dateCreated', order: 'desc')
    }


}
