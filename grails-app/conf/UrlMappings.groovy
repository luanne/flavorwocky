class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
        "/ping" (controller: "testBed", action:"ping")
        "/autosearch" (controller: "testBed", action:"autosearch")
	}
}
