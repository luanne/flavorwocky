/*
Copyright (c) 2012, Luanne Misquitta
All rights reserved. See License.txt
 */
package com.herokuapp.flavorwocky

import groovyx.net.http.RESTClient
import net.sf.json.JSONNull
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.ContentType.JSON

class FlavorwockyController {

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

    def ping() {
        def serverEndpointOk = false
        //lets just count the Categories and if they are more than 0 then we assume the server is reachable and initialized.
        if (Category.count()>0) {
            serverEndpointOk = true
        }
        render serverEndpointOk
    }

    def index() {
        def categories = Category.list()
        //affinity values are hard-coded over here for simplicity
        [categories: categories, affinity: [0.35: 'Tried and tested', 0.45: 'Extremely good', 0.6: 'Good']]
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
            return true
        }

        return false
    }

    private RESTClient createRESTClient(String uri) {
        def restClient = new RESTClient(uri)
        restClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
        return restClient
    }

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

    private List getChildren(int depth, int nodeId, int parentNodeId) {
        def childrenList = []

        if (depth > 3) {
            return null
        }

        def res = Ingredient.cypherStatic ("""start n=node({nodeId}), original=node({original})
                                              match (n)-[r:pairings]-(i)-[:category]->(cat)
                                              where not(i=original)
                                              return i.name as name ,cat.catColor as catColor ,ID(i)""",
                                            [nodeId: nodeId, original: parentNodeId]
                                          )
        res.iterator().each {
            def child = ["name": it.name, "catColor": it.catColor, "wt": 0.5]
            childrenList.add child
        }

        return childrenList
    }

    def getSearchVisualizationAsNetworkJson() {
        if (params.nodeId) {
            def nodeId = Integer.parseInt(params.nodeId)
            def cypherClient = createRESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/cypher")
            def queryStr = 'start n=node({nodeId}) match (n)-[p1:PAIRS_WITH]-(i1)-[p2?:PAIRS_WITH]-(i2)-[p3?:PAIRS_WITH]-(i3)-[p4?:PAIRS_WITH]-(i4)-[p5?:PAIRS_WITH]-(i5) ' +
                    ', (n)-[:IS_A]->(c0),(i1)-[?:IS_A]->(c1), (i2)-[?:IS_A]->(c2),(i3)-[?:IS_A]->(c3),(i4)-[?:IS_A]->(c4),(i5)-[?:IS_A]->(c5) ' +
                    ' return n.name,c1.catColor?,p1.wt?,i1.name?,c2.catColor?,p2.wt?,i2.name?,c3.catColor?,p3.wt?,i3.name?,c4.catColor?,p4.wt?,i4.name?,c5.catColor?,p5.wt?,i5.name?'
            def postBody = [query: queryStr,
                    params: ['nodeId': nodeId]]
            def nodeJsonArray = []
            def relationJsonArray = []
            try {
                def queryResp = cypherClient.post(contentType: JSON, requestContentType: JSON, body: postBody)
                if (queryResp.status == 200) {
                    def nodeIndex = [:]
                    def relationshipIndex = [:]

                    def nodeCounter = 0
                    def srcIngredient = null

                    for (row in queryResp.data.data) {
                        srcIngredient = null
                        for (i in 0..5) {
                            def ingredient = row.get(3 * i)
                            if (ingredient instanceof JSONNull) {
                                break
                            }

                            if (!nodeIndex.containsKey(ingredient)) {
                                nodeIndex.put(ingredient, nodeCounter)
                                def catColor = "black"
                                if (i != 0) {
                                    catColor = row.get((3 * i) - 2)

                                }
                                def nodeProps = ["name": ingredient, "catColor": catColor]
                                nodeJsonArray.add(nodeProps)
                                nodeCounter++
                            }
                            if (srcIngredient != null) {
                                float distance = Float.parseFloat(row.get((3 * i) - 1))
                                mapRelation(srcIngredient, ingredient, relationshipIndex, relationJsonArray, nodeIndex, distance)
                            }
                            srcIngredient = ingredient
                        }
                    }

                }
            }
            catch (ConnectException ce) {
                log.error "Connection to server failed"
                log.error ce
            }
            def finalStructure = ["nodes": nodeJsonArray, "links": relationJsonArray]
            render finalStructure as grails.converters.JSON
        } else {
            render "error"
        }
    }

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


}
