environments {
    development {
        grails {
            neo4j {
                type = "rest"
                location = "http://localhost:7474/db/data/"
            }
        }
    }
    test {
        grails {
            neo4j {
                type = "rest"
                location = "http://localhost:7474/db/data/"
            }
        }
    }
    production {
        grails {
            neo4j {
                type = "rest"
                location = "dummy"
            }
        }
    }

}

