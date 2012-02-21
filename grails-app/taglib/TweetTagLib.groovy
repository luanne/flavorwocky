
/**
 * Tweet buttons for the site
 */
class TweetTagLib {
    def tweet = {attrs, body ->
        out << """
<a href="https://twitter.com/share" class="twitter-share-button" data-url="http://flavorwocky.herokuapp.com" data-text="I love Flavorwocky- Neo4J &amp; Heroku Challenge App" data-via="luannem" data-size="large" data-count="none" data-hashtags="neo4jchallenge">Tweet</a>
<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>            </div>
"""
    }
}
