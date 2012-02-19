<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Flavorwocky"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">
		<r:require module="jquery-ui"/>
		<g:layoutHead/>
        <r:layoutResources />
	</head>
	<body>
		<div id="grailsLogo" role="banner">
		    <a id="logo" href="${grailsApplication.config.grails.serverURL}">Flavorwocky</a>
		    <div id="ping-ok" class="ui-widget neo4j-ping">
                <div class="ui-state-highlight ui-corner-all" style="margin-top: 2px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                    Neo4J server ping - <strong>OK!<strong>
					</p><p style="text-align:center">
					<a class="ping-server" href="#">Ping server</a>
					</p>
                </div>
            </div>
            <div id="ping-fail" class="ui-widget neo4j-ping">
				<div class="ui-state-error ui-corner-all" style="margin-top: 2px; padding: 0 .7em;">
					<p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
					Neo4J server ping - <strong>failed!</strong>
					</p><p style="text-align:center">
					<a class="ping-server" href="#">Ping server</a>
					</p>
				</div>
			</div>

		</div>
		<g:layoutBody/>
		<div class="footer" role="contentinfo"></div>
		<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
		<g:javascript library="application"/>
        <r:layoutResources />
	</body>
</html>