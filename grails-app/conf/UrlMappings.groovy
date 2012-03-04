/*
Copyright (c) 2012, Luanne Misquitta
All rights reserved. See License.txt
 */
class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:"flavorwocky")
		"500"(view:'/error')
        "/ping" (controller: "flavorwocky", action:"ping")
        "/autosearch" (controller: "flavorwocky", action:"autosearch")
        "/flavorTree" (controller: "flavorwocky", action:"getSearchVisualizationAsTreeJson")
        "/flavorNetwork" (controller: "flavorwocky", action:"getSearchVisualizationAsNetworkJson")
        "/flavorTrios" (controller: "flavorwocky", action:"getFlavorTrios")
        "/latestPairings" (controller: "flavorwocky", action:"getLatestPairings")
        "/about" (view: '/about')
        "/help" (view:  '/help')
	}
}
