<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>About</title>
	</head>
	<body>

        <h1>About Flavorwocky</h1>
        <p class="helptext">
         This app models ingredient pairs or flavor affinities and is my entry for the <a href="http://neo4j-challenge.herokuapp.com/" target="_blank">Neo4j Heroku Challenge</a>.
          <br/><br/>
                 Why <i>Flavorwocky</i>? Well, I said "what should I call the app?", and my husband said "<a href="http://en.wikipedia.org/wiki/Jabberwocky">jabberwocky</a>". It couldn't be called Jabberwocky of course, so behold- Flavorwocky.
         </p>

        <p class="helptext">
         It uses Grails 2.0 and the Neo4j add-on for Heroku to provide a basic template for anyone who wants to get up and running with Grails and Neo4j on Heroku.
          Unfortunately, the <a href="http://grails.org/plugin/neo4j" target="_blank">Grails Neo4j plugin</a> does not appear to work with Grails 2.0, so Grails ended up being used for the front end.
          The neat visualization is thanks to the <a href="http://mbostock.github.com/d3/" target="_blank">D3.js</a> library.
         </p>

        <p class="helptext">
        A graph is a good fit for this domain, so, using neo4j, we model the way ingredients in a great dish pair together based on their flavor affinity.<br/>
         The current version of this app captures flavor pairings and how well ingredients pair together, and then allows one to search by ingredient and view complementary combinations. Needless to say, this just scratches the surface of the possible features for an app like this.
       <br/><br/>

       I thought this app should focus completely on Neo4J so you won't find fancy bells and third party integrations (yet).
       Read more about what features are used in out blog <link>
       <<link>>

        </p>
	</body>
</html>