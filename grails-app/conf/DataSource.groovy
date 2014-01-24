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
                //location = System.getenv('NEO4J_REST_URL') ?: "http://localhost:7474/db/data/"
                location = System.getenv('GRAPHENEDB_URL') ?: "http://localhost:7474/db/data/"
            }
        }
    }

}

