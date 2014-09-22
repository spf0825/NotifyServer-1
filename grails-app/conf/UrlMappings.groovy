class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		//"/"(view:"/index")
        "/"{
            controller="info"
            action="indexTo"
        }

        "500"(view:'/error')
	}
}
