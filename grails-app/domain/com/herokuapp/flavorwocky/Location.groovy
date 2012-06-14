package com.herokuapp.flavorwocky

import groovy.transform.ToString

@ToString (includes = 'name')
class Location {

    static mapWith = "neo4j"

    String locationId
    String name


    static constraints = {
    }

}

