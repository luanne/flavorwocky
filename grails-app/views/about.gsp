<%
/*
Copyright (c) 2012, Luanne Misquitta
All rights reserved. See License.txt
 */
%>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>About</title>
        <script type="text/javascript">

          var _gaq = _gaq || [];
          _gaq.push(['_setAccount', 'UA-32833228-1']);
          _gaq.push(['_trackPageview']);

          (function() {
            var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
          })();

        </script>
	</head>
	<body>

        <h1>About Flavorwocky</h1>
        <p class="helptext">
         This app models ingredient pairs or flavor affinities and is my entry for the <a href="http://neo4j-challenge.herokuapp.com/" target="_blank">Neo4j Heroku Challenge</a>.
          <br/><br/>
                 Why <i>Flavorwocky</i>? Well, I said "what should I call the app?", and my husband said "<a href="http://en.wikipedia.org/wiki/Jabberwocky">jabberwocky</a>". It couldn't be called Jabberwocky of course, so behold- Flavorwocky.
             <br/><br/>
         It uses Grails 2.0 and the Neo4j add-on for Heroku to provide a basic template for anyone who wants to get up and running with Grails and Neo4j on Heroku.
          The neat visualization is thanks to the <a href="http://mbostock.github.com/d3/" target="_blank">D3.js</a> library.
            <br/><br/>
        A graph is a good fit for this domain, so, using Neo4j, we model the way ingredients in a great dish pair together based on their flavor affinity.<br/>
         The current version of this app captures flavor pairings and how well ingredients pair together, and then allows one to search by ingredient and view complementary combinations. Needless to say, this just scratches the surface of the possible features for an app like this.
       <br/><br/>

       I thought this app should focus completely on Neo4j so you won't find fancy bells and third party integrations (yet).
       Read more about what features are used in our blog:
       <br/><a href="http://thought-bytes.blogspot.in/2012/02/flavor-of-month-neo4j-add-on-for-heroku.html">Flavor of the month- Neo4j add-on for Heroku</a><br/>
       <a href="http://thought-bytes.blogspot.in/2012/02/flavor-of-month-neo4j-and-heroku-part-2.html">Flavor of the month- Neo4j add-on for Heroku, part 2</a>
       <br/> <br/>
        Feel free to add <a href="https://github.com/luanne/flavorwocky/issues?sort=created&direction=desc&state=open" target="_blank">issues</a> or contact me via the <a href="http://thought-bytes.blogspot.com" target="_blank">blog</a> or <a href="https://twitter.com/#!/luannem" target="_blank">twitter</a>.
        <br/>
        -Luanne Misquitta

        </p>
	</body>
</html>