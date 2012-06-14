package com.herokuapp.flavorwocky

import groovy.transform.ToString

@ToString (includes = 'name')
class User {

    static mapWith = "neo4j"

    String name
    String userId
    Location location

    static hasMany = [creates: Pairing]

    static constraints = {
        name (blank: false)
        userId (blank: false)
    }

}

