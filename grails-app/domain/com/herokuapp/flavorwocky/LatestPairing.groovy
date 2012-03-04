package com.herokuapp.flavorwocky

import groovy.transform.ToString

/**
 * Stores the 5 recent pairings. The node id and the pairing text is available, mainly to support UI display
 */
@ToString
class LatestPairing {

    static mapWith = "neo4j"

    int nodeId
    String pairing
    Date dateCreated

    static constraints = {
        nodeId (blank: false)
        pairing (blank: false)
    }
}
