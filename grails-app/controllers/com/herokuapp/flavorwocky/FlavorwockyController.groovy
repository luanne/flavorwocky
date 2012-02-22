package com.herokuapp.flavorwocky

import groovyx.net.http.RESTClient
import net.sf.json.JSONNull
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.ContentType.JSON

class FlavorwockyController {

    def autosearch() {
        def results
        try {
            def neo4jSearchClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/index/node/ingredients?query=name:${params.term.toLowerCase()}*")
            neo4jSearchClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
            def resultsResp = neo4jSearchClient.get (contentType:JSON, requestContentType:JSON)
            if (resultsResp.status == 200) {
                render(contentType: "text/json") {
                    results = array {
                        for (r in resultsResp.data) {
                            result id: Integer.parseInt(r.self.substring(r.self.lastIndexOf('/')+1)), name: r.data.name, label: r.data.name
                        }
                    }
                }
            }
        } catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
            render(contentType: "text/json") {
                error: ce.toString()
            }
        }

    }

    def ping () {
        def serverEndpointOk = false
        try {
            def pingClient = new RESTClient(grailsApplication.config.neo4j.rest.serverendpoint)
            pingClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
            def pingResp = pingClient.get (contentType:JSON, requestContentType:JSON)
            if (pingResp.status == 200) {
                serverEndpointOk = true
            }
        } catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
        }

        render serverEndpointOk
    }

    def index() {
        def categories
        //fetch the categories. This is assumed to be 'CATEGORY' type relationships with node 0
        try {
            def neo4jTraverseClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node/0/traverse/node")
            neo4jTraverseClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
            def postBody = [order: 'breadth_first', relationships: [ direction:'all', type:'CATEGORY'], max_depth: 1]
            def traverseResp = neo4jTraverseClient.post (contentType:JSON, requestContentType:JSON , body: postBody)
            if (traverseResp.status == 200) {
                categories = traverseResp.data.collectEntries { [Integer.parseInt(it.self.substring(it.self.lastIndexOf('/')+1)), it.data.name] }
            }

        } catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
        }

        [categories: categories, affinity:[0.35:'Tried and tested', 0.45:'Extremely good', 0.6:'Good']]
    }

    /**
     * Fetches the nodes references for ingredients if they exists, else creates them
     * @return List of the node references
     */
    private List fetchOrCreateNodes(RESTClient restClient, String ingredient1, String ingredient2, String categoryNode1, String categoryNode2) {
        List nodeRef = []
        def escapedIngredient1 = ingredient1.replace(" ","%20")
        def escapedIngredient2 = ingredient2.replace(" ","%20")
        try {
            //fetch the ingredients if they exist
            def postBody = [
                [method: 'GET', to: '/index/node/ingredients/name/'+escapedIngredient1.toLowerCase(), id: 0],
                [method: 'GET', to: '/index/node/ingredients/name/'+escapedIngredient2.toLowerCase(), id: 1]
            ]
            def createResp = restClient.post (contentType:JSON, requestContentType:JSON , body: postBody)
//            println "createResp.status = $createResp.status"
            if (createResp.status == 200) {
                postBody = []
                createResp.data.body.self.eachWithIndex {selfArr, i->
                    println "selfArr = $selfArr"
                    if (selfArr.size()<=0) {
                        //no such node exists, so create one
                        postBody.add([method: 'POST', to: '/node',  body: [name: i==0?ingredient1:ingredient2], id: i*3])
                        //IS-A relationship to Category
                        println "category1 = $categoryNode1"
                        println "category2 = $categoryNode2"
                        postBody.add([method: 'POST',
                                      to: "{${(i*3)}}/relationships".toString(),
                                      body: [to: grailsApplication.config.neo4j.rest.serverendpoint + '/' + (i==0?categoryNode1:categoryNode2), type: 'IS_A'],
                                      id:  i*3+1])
                        postBody.add([method: 'POST',
                                      to: '/index/node/ingredients?unique',
                                      body: [value: i==0?ingredient1.toLowerCase():ingredient2.toLowerCase(), key: 'name', uri:"{${i*3}}".toString()],
                                      id:  i*3+2])
                    } else {
                        //already exists, so just return the value
                        println "...already exists so just adding to noderef "
                        nodeRef.add selfArr[0] //if there are more than one then it is probably an error!!
                        println "nodeRef = $nodeRef"
                    }
                }
                if (postBody.size()>0) {
                    createResp = restClient.post (contentType:JSON, requestContentType:JSON , body: postBody)
                    println "createResp.status = $createResp.status"
                    if (createResp.status == 200) {
//                        println "createResp = $createResp.data"
                        println "...created a new node and now adding to noderef"
                        
                        createResp.data.each {
                            //we know that the id of the created node is either 0 or 3 since we send it with the batch request
                            println "it.id = "+it.id
                            if (it.id == 0 || it.id == 3) {
                                nodeRef.add it.body.self
                            }
                        }
                        //nodeRef.add createResp.data.body.self
                        println "nodeRef = $nodeRef"
                    }
                }
            }

        } catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
        }

        return nodeRef
    }

    private boolean createRelationship(String from, String to, String relation, String affinity) {
        def cypherClient = createRESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/cypher")
        println "from = $from"
        println "to = $to"
        try {
            //check if this relation already exists
            def postBody = [query: 'start n1=node({node1}), n2=node({node2}) match (n1)-[r:PAIRS_WITH]-(n2) return count(r)',
                            params: ['node1': Integer.parseInt(from.substring(from.lastIndexOf('/')+1)), 'node2': Integer.parseInt(to.substring(to.lastIndexOf('/')+1))]]
            def createResp = cypherClient.post (contentType:JSON, requestContentType:JSON , body: postBody)
            println "createResp.status = $createResp.status"
            if (createResp.status == 200) {
                println "createResp = $createResp.data"
                println "createResp data size = ${createResp.data.data.size()}"
                if (createResp.data.data.size()<=0) {
                    //doesn't exist, so now create it
                    def createClient = createRESTClient("${from}/relationships")
                    postBody = [to: to, type: relation, data: [wt: affinity] ]
                    createResp = createClient.post(contentType:JSON, requestContentType:JSON , body: postBody)
                    if (createResp.status == 200) {
                        return true
                    }
                    else {
                        return false
                    }
                }
                return true
            }

        } catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
        }

        return false

    }

    def create() {
        if (!params.ingredient1 || !params.ingredient2 || !params.category1 || !params.category2 || !params.affinity) {
            render "Invalid parameter values"
            return
        }

        //Experimental Batch feature. Note: this part of the API is expected to change
        def createClient = createRESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/batch")
        def nodeRef = fetchOrCreateNodes(createClient, params.ingredient1, params.ingredient2, params.category1, params.category2)
        //create a PAIRS_WITH relationship between node 1 and node 2 if it doesn't already exist
//        println "nodeRef = $nodeRef"
        createRelationship(nodeRef[0], nodeRef[1], 'PAIRS_WITH', params.affinity)

        render "done"
    }

    private RESTClient createRESTClient(String uri) {
        def restClient = new RESTClient(uri)
        restClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
        return restClient
    }

    def getSearchVisualizationAsTreeJson () {
        println "params = $params"
        if (params.nodeId) {
            def nodeId = Integer.parseInt(params.nodeId)  //Integer.parseInt(params.nodeId.substring(params.nodeId.lastIndexOf('/')+1))
            List children =  getChildren(1, nodeId, nodeId)
            
            def cypherClient = createRESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/cypher")
            def queryStr =  'start n=node({nodeId}) match (n)-[:IS_A]->(cat) return cat.catColor, n.name'
            def postBody = [query: queryStr,
                    params: ['nodeId': nodeId]]
            def catColor='lightsteelblue'
            def ingredientName
            try {
                def getNodeResp = cypherClient.post(contentType: JSON, requestContentType: JSON, body: postBody)
                if (getNodeResp.status == 200) {
                    catColor = getNodeResp.data.data.get(0).get(0)
                    ingredientName = getNodeResp.data.data.get(0).get(1)
                }
            }
            catch (ConnectException ce) {
                log.error "Connection to server failed"
                log.error ce
            }

            def finalStructure = ["name":ingredientName,"catColor":catColor,"wt":1,"children" : children]

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

        def cypherClient = createRESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/cypher")
        def queryStr =  'start n=node({nodeId}), original=node({original}) match (n)-[r:PAIRS_WITH]-(i)-[:IS_A]->(cat) where not(i=original) return i.name,cat.catColor,ID(i),r.wt'
        def postBody = [query: queryStr,
            params: ['nodeId': nodeId, 'original' : parentNodeId]]

        try {
            def queryResp = cypherClient.post(contentType: JSON, requestContentType: JSON, body: postBody)
            if (queryResp.status == 200) {

                for (row in queryResp.data.data) {
                    def child = ["name":row.get(0),"catColor":row.get(1),"wt":row.get(3)]
                    child.put("children",getChildren(depth+1,row.get(2),nodeId))
                    childrenList.add(child)
                }

            }
        }
        catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
        }

        return childrenList
    }

    def getSearchVisualizationAsNetworkJson () {
       // println "params = $params"
        if (params.nodeId) {
            def nodeId = Integer.parseInt(params.nodeId)
            def cypherClient = createRESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/cypher")
            def queryStr =  'start n=node({nodeId}) match (n)-[p1:PAIRS_WITH]-(i1)-[p2?:PAIRS_WITH]-(i2)-[p3?:PAIRS_WITH]-(i3)-[p4?:PAIRS_WITH]-(i4)-[p5?:PAIRS_WITH]-(i5) ' +
                    ', (n)-[:IS_A]->(c0),(i1)-[?:IS_A]->(c1), (i2)-[?:IS_A]->(c2),(i3)-[?:IS_A]->(c3),(i4)-[?:IS_A]->(c4),(i5)-[?:IS_A]->(c5) ' +
                    ' return n.name,c1.catColor?,p1.wt?,i1.name?,c2.catColor?,p2.wt?,i2.name?,c3.catColor?,p3.wt?,i3.name?,c4.catColor?,p4.wt?,i4.name?,c5.catColor?,p5.wt?,i5.name?'
            def postBody = [query: queryStr,
                    params: ['nodeId': nodeId]]
        def nodeJsonArray = []
        def relationJsonArray = []
            try {
                def queryResp = cypherClient.post(contentType: JSON, requestContentType: JSON, body: postBody)
                if (queryResp.status == 200) {
                    def nodeIndex =[:]
                    def relationshipIndex = [:]

                    def nodeCounter = 0
                    def srcIngredient=null

                    for (row in queryResp.data.data) {
                        srcIngredient = null
                        for (i in 0..5) {
                            def ingredient = row.get(3*i)
                            if (ingredient instanceof JSONNull) {
                                break
                            }

                            if(!nodeIndex.containsKey(ingredient)) {
                                nodeIndex.put(ingredient,nodeCounter)
                                def catColor="black"
                                if(i !=0) {
                                    catColor=row.get((3*i)-2)
                                    println "catColor = $catColor"

                                }
                                def nodeProps = ["name": ingredient,"catColor":catColor]
                                nodeJsonArray.add(nodeProps)
                                nodeCounter++
                            }
                            if(srcIngredient!=null) {
                                float distance = Float.parseFloat(row.get((3*i)-1))
                                mapRelation(srcIngredient,ingredient,relationshipIndex,relationJsonArray,nodeIndex,distance)
                            }
                            srcIngredient=ingredient
                        }
                    }

                }
            }
            catch (ConnectException ce) {
                log.error "Connection to server failed"
                log.error ce
            }
            def finalStructure = ["nodes":nodeJsonArray,"links":relationJsonArray]
            render finalStructure as grails.converters.JSON
        } else {
            render "error"
        }
    }

    def mapRelation(String src, String target, Map relationshipIndex, List relationJsonArray, Map nodeIndex, float distance) {

        if(relationshipIndex.containsKey(src)) {
            if(relationshipIndex.get(src).contains(target)) {
                return
            }
            else {
                relationshipIndex.get(src).add(target)
            }
        }
        else {
            def targetList = [target]
            relationshipIndex.put(src,targetList)
        }
        def relationProps = ["source":nodeIndex.get(src),"target":nodeIndex.get(target),"dist":distance]
        relationJsonArray.add(relationProps)

    }


}
