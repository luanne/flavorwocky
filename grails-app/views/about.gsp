<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>About</title>
	</head>
	<body>

        <h1>About Flavors</h1>
        <p class="helptext">
         This app is inspired by the book <a href="http://www.amazon.com/Flavor-Bible-Essential-Creativity-Imaginative/dp/0316118400/ref=cm_cr_pr_product_top" target="_blank">The Flavor Bible</a> by Karen Page and Andrew Dornenburg and is our entry for the <a href="http://neo4j-challenge.herokuapp.com/" target="_blank">Neo4j Heroku Challenge</a>.
         </p>

        <p class="helptext">
         It uses Grails 2.0 and the Neo4j add-on for Heroku to provide a basic template for anyone who wants to get up and running with Grails and Neo4j on Heroku. The neat visualization is thanks to the <a href="http://mbostock.github.com/d3/" target="_blank">D3.js</a> library.
         </p>

        <p class="helptext">
        We model in neo4j, the way ingredients in a great dish pair together based on their flavor affinity, a great use case for a graph. <br/>
         The current version of this app captures flavor pairings and how well ingredients pair together, and then allows one to search by ingredient and view complementary combinations. Needless to say, this just scratches the surface of the possible features for an app like this.
       <br/><br/>
       Our graph model looks like this:

       <<insert graph>>

       Ingredient nodes have a name property, category nodes have a catColor property (used in the visualization, stored as a property for convenience), and the PAIRS_WITH relationship between ingredients has a weight property "wt" that indicates the affinity between the flavors of the two ingredients.
       <br/>
       The ingredient names are indexed into the "ingredients" index. <br/>
       Here are the features of the neo4J REST api used in this template:  <br/>

       Add Pairing:

       Get a list of all categories:

       Autocomplete:

       Search:






        </p>
	</body>
</html>