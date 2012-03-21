package com.herokuapp.flavorwocky

import groovy.transform.ToString

@ToString (includes = 'name')
class Ingredient {

    static mapWith = "neo4j"

    String name
    Category category

    static hasMany = [pairings: Ingredient]

    static constraints = {
    }

}

