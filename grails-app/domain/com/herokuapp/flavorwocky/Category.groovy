package com.herokuapp.flavorwocky

import groovy.transform.ToString

@ToString
class Category {

    static mapWith = "neo4j"

    String name //Category name
    String catColor //color for the UI display. just for convenience sake for the demo. never do this in a real app.


    static constraints = {
    }
}
