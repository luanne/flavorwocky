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
		<g:javascript library="jquery" />
		<script language="javascript">

        				if (window.location.href.indexOf("herokuapp")>0)
        				{
        					window.location.href="http://www.flavorwocky.com";
        				}

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
	    <form id="safe">
	        <input type="hidden" id="ingredientNodeId">
	    </form>
        <div id="prompts">
            <div class="promptLabel">
                Find ingredients that pair well with
                <br><div id="example">ex. Chicken, Coriander, Mustard, Onion</div>
            </div>
            <input id="food" />
            <div id="orPart">OR&nbsp;&nbsp;&nbsp;<button id="addPairing" ${!loggedIn?"disabled":""}>${!loggedIn?"Log in and Add Pairing":"Add Pairing"}</button>
                             <div id="success"></div>
            </div>
            <div id="searchFeedback" class="ui-state-error"></div>
        </div>



    <div id="pairing-dialog-form" title="Add a pairing" style="clear:both">
        <p class="validateTips"></p>
        <form>
        <fieldset>
            <div class="dialog-left">
                <label for="ingredient1">Ingredient 1</label>
                <input type="text" name="ingredient1" id="ingredient1" class="text ui-widget-content ui-corner-all" />
                <g:select name="category1" from="${categories}" optionKey="id" optionValue="name"/>
            </div>
            <div class="dialog-right">
                <label for="ingredient2">Ingredient 2</label>
                <input type="text" name="ingredient2" id="ingredient2" class="text ui-widget-content ui-corner-all" />
                <g:select name="category2" from="${categories}"  optionKey="id" optionValue="name"/>
            </div>
            <div class="dialog-somewhere">
                <label for="affinity">Affinity</label>
                <g:select name="affinity" from="${affinity}" optionKey="key" optionValue="value"/>
            </div>
        </fieldset>
        </form>
    </div>
    <div id="chart"></div>
    <div id="trios" class="ui-widget-content"></div>
    <div id="latest" class="ui-widget-content">
        <p>Freshly added</p>
        <ul>
        <g:each in="${latest}">
            <g:set var="key" value="${it.nodeId}" />
            <g:set var="val" value="${it.pairing}" />
            <li nodeid="${key}"><a href='#'>${val}</a><br/>by ${it.userName==null?"Anonymous":it.userName}</li>
        </g:each>
        </ul>
    </div>
    <div id="chartOptions">
        <div id="viewExploration">Explore</div>
        <div id="viewInteraction">View Interactions</div>
    </div>

        <script type="text/javascript">
            var autosearchLink = '${createLink(action:'autosearch')}'
            var createLink = '${createLink(action:'create')}'
            var whichView = 'tree'; //'tree' or 'network'
        </script>
        <script type="text/javascript" src="${resource(dir: 'js', file: 'd3.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js', file: 'd3.layout.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js', file: 'd3.geom.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js', file: 'viz.js')}"></script>
	</body>
</html>