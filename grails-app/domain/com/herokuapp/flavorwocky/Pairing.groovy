package com.herokuapp.flavorwocky

import groovy.transform.ToString

@ToString (includes = 'name')
class Pairing {

    static mapWith = "neo4j"

    long createdOnMillis  //Grails plugin stores a date as a string instead of a long
    Float affinity
    User user

    static hasMany = [hasIngredient: Ingredient]

    static constraints = {
        createdOnMillis (blank: false)
        affinity (blank: false)
    }

}

