package com.herokuapp.flavorwocky

import groovy.transform.ToString

@ToString (includes = 'name')
class Pairing {

    static mapWith = "neo4j"

    Date createdOn
    Float affinity

    static hasMany = [contains: Ingredient]

    static constraints = {
        createdOn (blank: false)
        affinity (blank: false)
    }

}

