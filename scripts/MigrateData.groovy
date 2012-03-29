/*
    Comment out the createInitialCategories() line in Bootstrap.groovy, before running this script
    open in >'grails console' and run
 */

import org.neo4j.graphdb.*
import com.herokuapp.flavorwocky.*

//for all categories
def graphDatabaseService = ctx.getBean('graphDatabaseService')
enum RelTypes implements RelationshipType
{
    CATEGORY,
    pairings,
    LATEST_PAIRS,
    IS_A,
    PAIRS_WITH
}


def node0 = graphDatabaseService.referenceNode

print "Migrating categories..."
//Migrate categories
def catMap = [:]
Category.withTransaction {
    //Create all the categories
    node0.getRelationships(Direction.OUTGOING, RelTypes.CATEGORY).each {
        catMap[it.endNode.getProperty('name')] = new Category(name: it.endNode.getProperty('name'), catColor: it.endNode.getProperty('catColor')).save(failOnError: true)
    }

/*
    Category.list().each {
        //it.delete()
        //println it
    }
*/
}
println "done"


def st1=System.currentTimeMillis()
print "Migrating ingredients..."
def ingrMap = [:]

//Migrate ingredients
Ingredient.withTransaction {
    Ingredient.cypherStatic('start n=node(0) match n-[:CATEGORY]-c1-[:IS_A]-n1 return c1.name, n1.name').each {
        def categoryInstance = catMap[it['c1.name']]?:Category.findByName(it['c1.name'])
        if (categoryInstance) {
            def ingName = it['n1.name'].toLowerCase()
            ingName = ingName[0]?.toUpperCase() + (ingName.length()>1?ingName.substring(1):'')
            def ingInstance = new Ingredient(name:ingName, category: categoryInstance).save(failOnError: true)
            ingrMap[ingName] = ingInstance
        }
        else {
            println "This ingredient doesn't have a category!!! ${it['n1.name']}"
        }
    }
}
def et1=System.currentTimeMillis()
print "- ${et1-st1} -"
println "done"


//Migrate pairings
print "Migrating pairings..."
def st2=System.currentTimeMillis()
Ingredient.withTransaction {
    Ingredient.cypherStatic('start n0=node(0) match (n0)-[:CATEGORY]->(c)<-[:IS_A]-(i1)-[p:PAIRS_WITH]->(i2) return i1.name, i2.name, p.wt')
            .each { pairing->
        //println "------------------"
        println "${pairing['i1.name']}  ::: ${pairing['i2.name']}  ::: ${pairing['p.wt']}"
        def ingredientAInstance = ingrMap[pairing['i1.name']]?:Ingredient.findByNameIlike(pairing['i1.name'])
        def ingredientBInstance = ingrMap[pairing['i2.name']]?:Ingredient.findByNameIlike(pairing['i2.name'])
        if (ingredientAInstance && ingredientBInstance) {
            if (!ingredientAInstance.pairings?.contains(ingredientBInstance) && !ingredientBInstance.pairings?.contains(ingredientAInstance)) {
                ingredientAInstance.addToPairings ingredientBInstance
                //println "adding the above pairing"
                //ingredientAInstance.save(flush: true)
                ingredientAInstance.node.getRelationships(RelTypes.pairings, Direction.OUTGOING).each {
                    if (it.endNode.id == ingredientBInstance.id) {
                        it.setProperty("wt", pairing['p.wt'])
                    }
                }
            }
        }
    }
}

def et2=System.currentTimeMillis()
print "- ${et2-st2} -"
println "done"


print "Migrating latest..."
//Migrate latest pairings
LatestPairing.withTransaction {
    node0.getRelationships(Direction.OUTGOING, RelTypes.LATEST_PAIRS).each {
        it.endNode.getProperty('pairs').each {pair->
            def tmp = pair.split(':')
            def n = graphDatabaseService.getNodeById(Long.valueOf(tmp[0]))
            def ingredientInstance = Ingredient.findByNameIlike(n.getProperty('name'))
            if (ingredientInstance) {
                new LatestPairing(nodeId: ingredientInstance.id, pairing: tmp[1]).save(failOnError: true)
            }
        }
    }
}
println "done"


//Delete the old ingredient nodes


//Delete the old category nodes
print "Deleting old nodes..."
Category.withTransaction {
    node0.getRelationships(Direction.OUTGOING, RelTypes.CATEGORY).each {categoryRel->
        //now for each category find the ingredients and delete them
        //println 'categoryRel = '+categoryRel
        categoryRel.endNode.getRelationships(Direction.INCOMING, RelTypes.IS_A).each {isa->
            //println isa.startNode
            isa.startNode.getRelationships(RelTypes.PAIRS_WITH).each {pairsWith->
                //print 'deleting pairswith '+pairsWith
                pairsWith.delete()
                //println ' DONE'
            }
            isa.delete()
            //print 'deleting isa.startNode '+isa.startNode
            isa.startNode.delete()
            //println ' DONE'
        }
        //print 'deleting category.endNode '+categoryRel.endNode
        categoryRel.delete()
        categoryRel.endNode.delete()
        //println ' DONE'
    }

    //delete latest_pairs
    node0.getRelationships(Direction.OUTGOING, RelTypes.LATEST_PAIRS).each {
        it.delete()
        it.endNode.delete()
    }

    //delete the ingredient index
    def index = graphDatabaseService.index()
    index.forNodes('ingredients')?.delete()
}
println "done"

println '--DONE--'