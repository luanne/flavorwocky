Flavorwocky
========

Flavorwocky was an entry for the [Neo4j Heroku Challenge](http://blog.neo4j.org/2012/03/neo4j-heroku-challenge-winner-and.html) 
and has been rewritten to use Spring Data Neo4j 4. 

Demo: http://www.flavorwocky.com

A guide to building Flavorwocky with Spring Data Neo4j 4: 

Deploying Locally
=================
* Set up Neo4j 2.2.x
* `> git clone git@github.com:luanne/flavorwocky.git`
* `> git checkout sdn`
* The application runs on Heroku and uses the GrapheneDB plugin. It has been set up to use the environment variable called GRAPHENEDB_URL. Create an environment variable on your machine called GRAPHENEDB_URL with value of the form http://<neo4j-server-username>:<neo4j-server-password@<neo4j-host>:<neo4j-port> e.g. http://neo4j:neo@localhost:7474 
* Make sure your local Neo4j server is running
* Create categories using the `categorySetup.cql` script
* `>mvn clean spring-boot:run`
* Launch the app in your browser (e.g. http://localhost:8080/)

