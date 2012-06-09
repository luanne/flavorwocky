import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.ContentType.JSON

private List getChildren(int depth, int nodeId) {
    def childrenList = []

    if (depth > 1) {
        return childrenList
    }

    def cypherClient = createRESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/cypher")
    def postBody = [query: 'start n=node({nodeId} match (n)-[:PAIRS_WITH*1..{depth}]-(i) return i.name',
            params: ['nodeId': nodeId, 'depth': depth]]

    try {
        def createResp = cypherClient.post(contentType: JSON, requestContentType: JSON, body: postBody)
        if (createResp.status == 200) {
            println "all ok"  + createResp.data
        }
    }
    catch (ConnectException ce) {
        log.error "Connection to server failed"
        log.error ce
    }


}

private RESTClient createRESTClient(String uri) {
    def restClient = new RESTClient(uri)
    restClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
    return restClient
}

getChildren(1,11)