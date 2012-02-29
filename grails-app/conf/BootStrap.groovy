/*
Copyright (c) 2012, Luanne Misquitta
All rights reserved. See License.txt
 */

import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.ContentType.JSON

class BootStrap {

    def grailsApplication

    /**
     * Initialize the app
     */
    def init = { servletContext ->
        //check if there are categories in the db
        try {
            def neo4jTraverseClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node/0/traverse/node")
            neo4jTraverseClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
            def postBody = [order: 'breadth_first', relationships: [direction: 'all', type: 'CATEGORY'], max_depth: 1]
            def traverseResp = neo4jTraverseClient.post(contentType: JSON, requestContentType: JSON, body: postBody)
            if (traverseResp.status == 200 && traverseResp.data.size() <= 0) { //If there are no categories then this is an empty db
                //create ingredient index
                createIngredientIndex()
                //create Category nodes
                createInitialCategories()
            }
            createLatestPairings() //Create the latest pairings node that holds latest pairings added, in case it does not exist.

        } catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
        }

    }


    def destroy = {
    }

    /**
     * Create categories and link to the reference node.
     */
    def createInitialCategories() {
        def neo4jCreateClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node")
        neo4jCreateClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password

        //ideally this should be batched.
        ['Fish': 'darkblue', 'Poultry': 'hotpink', 'Meat': 'firebrick', 'Herbs and spices': 'yellowgreen',
                'Condiments': 'goldenrod', 'Eggs and dairy': 'wheat', 'Vegetables': 'darkgreen', 'Fruits': 'lightcoral',
                'Nuts and Grains': 'orange', 'Chocolate, Bread and Pastry': 'saddlebrown'].each {
            def createResp = neo4jCreateClient.post(
                    body: [name: it.key, catColor: it.value],
                    requestContentType: JSON,
                    contentType: JSON)
            if (createResp.status == 201) {
                log.info "Created category node :: $it"
                def newCat = createResp.data.self
                //now create the relation with node 0
                def relationClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node/0/relationships")
                relationClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password

                def relationshipResponse = relationClient.post(
                        body: [to: newCat, type: 'CATEGORY'],
                        requestContentType: JSON,
                        contentType: JSON)
                if (relationshipResponse.status == 201) {
                    log.info "Created CATEGORY relationship to :: $it"
                    return
                }
                log.error "Could not create category relationship ::  $it"

            }
            log.error "Could not create category node :: $it"


        }
    }

    /**
     * Create the ingredient index
     */
    def createIngredientIndex() {
        def neo4jCreateIndexClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/index/node")
        neo4jCreateIndexClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
        def createResp = neo4jCreateIndexClient.post(
                body: [name: 'ingredients'],
                requestContentType: JSON,
                contentType: JSON)
        println createResp.status
        if (createResp.status == 201) {
            log.info "Created ingredients index"
            return
        }
        log.error "Could not create ingredient index"
    }

    /**
     * Create the latest pairings node which holds 5 pairings in a String array property on the node
     */
    def createLatestPairings() {
        def neo4jTraverseClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node/0/traverse/node")
        neo4jTraverseClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
        def postBody = [order: 'breadth_first', relationships: [direction: 'out', type: 'LATEST_PAIRS'], max_depth: 1]
        def traverseResp = neo4jTraverseClient.post(contentType: JSON, requestContentType: JSON, body: postBody)
        def pairNode
        if (traverseResp.status == 200) {
            if (traverseResp.data.size() <= 0) {
                log.info "Creating the latest pairing node..."
                def pairNodeClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node")
                pairNodeClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
                def createResp = pairNodeClient.post(
                        body: [name: "late"],
                        requestContentType: JSON,
                        contentType: JSON)
                if (createResp.status == 201) {
                    log.info "Created latest pairing node"
                    pairNode = createResp.data.self

                    //now create the relation with node 0
                    def relationClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node/0/relationships")
                    relationClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password

                    def relationshipResponse = relationClient.post(
                            body: [to: pairNode, type: 'LATEST_PAIRS'],
                            requestContentType: JSON,
                            contentType: JSON)
                    if (relationshipResponse.status == 201) {
                        log.info "Created LATEST_PAIRS relationship to"
                    }

                }

            }
        }
    }
}
