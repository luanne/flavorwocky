import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.ContentType.JSON

class BootStrap {

    def grailsApplication

    def init = { servletContext ->
        //check if there are categories in the db
        try {
            def neo4jTraverseClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node/0/traverse/node")
            neo4jTraverseClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
            def postBody = [order: 'breadth_first', relationships: [ direction:'all', type:'CATEGORY'], max_depth: 1]
            def traverseResp = neo4jTraverseClient.post (contentType:JSON, requestContentType:JSON , body: postBody)
            if (traverseResp.status == 200 && traverseResp.data.size()<=0) {
                //create Category nodes
                createInitialCategories()
            }

        } catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
        }

    }


    def destroy = {
    }

    def createInitialCategories() {
        def neo4jCreateClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node")
        //neo4jCreateClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password

        ['Fish', 'Poultry', 'Meat', 'Herbs and spices', 'Condiments', 'Eggs and dairy', 'Vegetables', 'Fruits'].each {
            def createResp = neo4jCreateClient.post(
                    body: [name: it],
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
                }

            }


        }
    }
}
