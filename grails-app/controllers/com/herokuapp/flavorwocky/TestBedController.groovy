package com.herokuapp.flavorwocky

import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.ContentType.JSON

class TestBedController {

    def autosearch() {
        println "params = $params"
        //render ['aaaaaa', 'bbbbbb'] as JSON
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
        def categories = []
        //fetch the categories. This is assumed to be 'CATEGORY' type relationships with node 0
        try {
            def neo4jTraverseClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node/0/traverse/node")
            neo4jTraverseClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
            def postBody = [order: 'breadth_first', relationships: [ direction:'all', type:'CATEGORY'], max_depth: 1]
            def traverseResp = neo4jTraverseClient.post (contentType:JSON, requestContentType:JSON , body: postBody)
            if (traverseResp.status == 200) {
                categories = traverseResp.data.collect { it.data.name }
            }

        } catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
        }

        [categories: categories]
    }

    def create() {
        println "params = $params"
        if (!params.ingredient1 || !params.ingredient2 || !params.category1 || !params.category2) {
            render "Invalid parameter values"
            return
        }

        def firstIngredientRelationshipNode
        def createClient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/node")
        def indexIngredient = new RESTClient("${grailsApplication.config.neo4j.rest.serverendpoint}/index/node/ingredients?unique")
        createClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
        indexIngredient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
        try {
            def postBody = [name: params.ingredient1, category: params.category1]
            def createResp = createClient.post (contentType:JSON, requestContentType:JSON , body: postBody)
            if (createResp.status == 201) {
                def ingredientDetails = createResp.data
                //println ingredientDetails
                firstIngredientRelationshipNode = ingredientDetails.create_relationship
                //Create BELONGS_TO relationship with category
                //Index the ingredient
                indexIngredient.post(contentType: JSON,requestContentType: JSON, body:  [value: params.ingredient1, key: 'name',uri:ingredientDetails.self])

            }
        } catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
        }

        def secondIngredient
        try {
            def postBody = [name: params.ingredient2, category: params.category2]
            def createResp = createClient.post (contentType:JSON, requestContentType:JSON , body: postBody)
            if (createResp.status == 201) {
                def ingredientDetails = createResp.data
                secondIngredient = ingredientDetails.self
                //Create BELONGS_TO relationship with category
                //Index the ingredient
                indexIngredient.post(contentType: JSON,requestContentType: JSON, body:  [value: params.ingredient2, key: 'name',uri:ingredientDetails.self])

            }

        } catch (ConnectException ce) {
            log.error "Connection to server failed"
            log.error ce
        }

        //now create a relationship between the two
        //{"to": "http://localhost:7474/db/data/node/14","type":"PAIRS_WITH"}
        println "firstIngredientRelationshipNode = $firstIngredientRelationshipNode"
        println "secondIngredient = $secondIngredient"
        def firstRelationshipClient = new RESTClient(firstIngredientRelationshipNode)
        firstRelationshipClient.auth.basic grailsApplication.config.neo4j.rest.username, grailsApplication.config.neo4j.rest.password
        def createResp = firstRelationshipClient.post(
                body: [to: secondIngredient, type: 'PAIRS_WITH'],
                requestContentType: JSON,
                contentType: JSON)
        if (createResp.status != 201) {
            log.error "Could not create PAIRS_WITH relationship between ${firstIngredientRelationshipNode} and ${secondIngredient} :: ${createResp.status}"
        }


        render "done"
        
    }

}
