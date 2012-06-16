/*
Copyright (c) 2012, Luanne Misquitta
All rights reserved. See License.txt
 */
package com.herokuapp.flavorwocky

import net.sf.json.JSONArray
import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.RelationshipType
import grails.plugins.facebooksdk.FacebookGraphClient

class FlavorwockyController {

    def facebookAppService
    def facebookClient

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
        //lets just count the Categories and if there are more than 0 then we assume the server is reachable and initialized.
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
        def loggedIn
        if(session.userId) {
            loggedIn=true
        }
        def categories = Category.list()
        //affinity values are hard-coded over here for simplicity
        [categories: categories,
                affinity: [0.35: 'Tried and tested', 0.45: 'Extremely good', 0.6: 'Good'],
                latest: getLatestPairings(),
                loggedIn: loggedIn
        ]
    }

    def login() {
        if(facebookAppService.userId) {
            session.userId = facebookAppService.userId
            facebookClient = new FacebookGraphClient(facebookAppService.getUserAccessToken())
            def user = facebookClient.fetchObject("me")
            session.userName=user.name
            session.location = user.location
        }
        redirect(action: 'index')

    }

    def logout() {
        session.removeAttribute('userId') //somehow the session is not invalidated in the below call
        //facebookAppService.invalidateUser()
        redirect(action:'index')
    }

    /**
     * Fetches the nodes references for ingredients if they exists, else creates them
     * @return List of the node references
     */
    private def fetchOrCreateNodes(String ingredient1, String ingredient2, String categoryNode1, String categoryNode2) {
        def storedIngredients = Ingredient.findAllByNameInList([ingredient1, ingredient2])
        def ingredient1Instance = storedIngredients.find {it.name==ingredient1}
        def ingredient2Instance = storedIngredients.find {it.name==ingredient2}
        if (!ingredient1Instance) {
            ingredient1Instance = new Ingredient(name: ingredient1, category: Category.get(categoryNode1)).save(failOnError: true)
        }

        if (!ingredient2Instance) {
            ingredient2Instance = new Ingredient(name: ingredient2, category: Category.get(categoryNode2)).save(failOnError: true)
        }

        return [ingredient1Instance, ingredient2Instance] //as List<Ingredient>
    }


    /**
     * Fetches a location, else creates it
     * @return User
     */
    private Location fetchOrCreateLocation() {
        def location = Location.findByLocationId(session.location.id)
        if(!location) {
            location = new Location(locationId: session.location.id, name:  session.location.name).save(failOnError: true)
        }

        return location

    }

    /**
     * Fetches the user who created the pairing, else creates him
     * @return User
     */
    private User fetchOrCreateUser() {
        def user = User.findByUserId(session.userId)
        println "user = $user"


        if (!user) {
            def location
            if(!session.location==null) {
                location=fetchOrCreateLocation()
            }
println "location $location"
            user = new User(userId: session.userId, name: session.userName, location: location).save(failOnError: true)
        }

        return user
    }

    /**
     * Create flavor pair
     */
    def create(String ingredient1, String ingredient2, String category1, String category2, Float affinity) {
        if (!ingredient1 || !ingredient2 || !category1 || !category2 || !affinity) {
            render "Invalid parameter values"
            return
        }

        Ingredient.withTransaction {

            def pairedIngredients = fetchOrCreateNodes(ingredient1, ingredient2, category1, category2)
            println "here 3"
            createRelationship(pairedIngredients, affinity)
            println "here 4"
            def user = fetchOrCreateUser()
            println "affinity = $affinity"
            println "affinity = ${affinity.class}"
            def pairing = new Pairing(createdOnMillis: new Date().time, affinity: affinity).save(failOnError: true)

            //println "pairing $pairing"
            pairedIngredients.each{
                println "it = ${it.class}"
                pairing.addToContains1 it
            }
            println "here 5"
            pairing.save()
            println "here 6"
            user.addToCreates pairing
            println "here 7"
            user.save()
            println "here 8"
        }

        render "done"
    }

    /**
     * Creates a 'pairings' relationship between pairedIngredients
     * @param pairedIngredients List of 2 ingredients
     * @return true if a relationship was created, false if a relationship already exists
     */
    private boolean createRelationship(List<Ingredient> pairedIngredients, Float affinity) {
        def res = Ingredient.cypherStatic("start n1=node({node1}), n2=node({node2}) match (n1)-[r:pairings]-(n2) return count(r)",
                                    [node1: pairedIngredients[0]?.id, node2: pairedIngredients[1]?.id]
                                )
        if (res.iterator().size() <=0 ) {
            //no relationship exists between these two ingredients, so lets create one
            pairedIngredients[0].addToPairings(pairedIngredients[1])

            /* Relationship properties are not supported by the neo4j plugin. so here is a how to do it using the java api */
            pairedIngredients[0].save(flush: true)
            pairedIngredients[0].node.getRelationships(PairingRelationshipType.pairings, Direction.OUTGOING).each {
                if (it.endNode.id == pairedIngredients[1].id) {
                    it.setProperty("wt", affinity)
                }
            }

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

        println "paired0" + pairedIngredients[0].id
        println "paired1" + pairedIngredients[1].id

        new LatestPairing(nodeId: pairedIngredients[0].id, pairing: pairedIngredients[0].name + " and " + pairedIngredients[1].name)
            .save(failOneError: true)
        print "after new latest pairing"
    }

    /**
     * Return search results as a tree
     * @return JSON for tree visualization
     */
    def getSearchVisualizationAsTreeJson() {
        //TODO this is a really bad, inefficient way to build the JSON tree and is only good for demos.
        //TODO refactor for production or else be forever ashamed
        if (params.nodeId) {
            def nodeId = Integer.parseInt(params.nodeId)
            List children = getChildren(1, nodeId, nodeId)
            def rootIngredient = Ingredient.get(nodeId)
            def finalStructure = ["name": rootIngredient.name, "catColor": rootIngredient.category.catColor, "wt": 1, "children": children]
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
                                              return i.name as name ,cat.catColor as catColor ,ID(i) as idi, r.wt as wt""",
                                            [nodeId: nodeId, original: parentNodeId]
                                          )
        res.iterator().each {
            def child = ["name": it.name, "catColor": it.catColor, "wt": it.wt]
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
                                    def distance = it["p${i+1}.wt"]
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
    private void mapRelation(String src, String target, Map relationshipIndex, List relationJsonArray, Map nodeIndex, def distance) {

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

    enum PairingRelationshipType implements RelationshipType
    {
        pairings
    }
}
