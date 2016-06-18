(ns jobluv.handlers.tldr
  (:require 
  			[clojure.string :as str]
            [ring.util.response :refer [response]]
            [org.httpkit.client :as http]
  		  	[clojure.data.json :as json])
)

(defn structure-tldr-response [keywords tldr]
 	(response 
 		{
	 		:color "green"
	 		:message (format "Keywords: %s                 TLDR:  %s " keywords tldr)
	 		:notify false
	 		:message_format "text"
 		}
 	)
)

(defn structure-error-response [message]
 	(response 
 		{
	 		:color "red"
	 		:message (format "I played myself.\n Error: %s" message)
	 		:notify false
	 		:message_format "text"
 		}
 	)
)

(defn arrange-tldr [body]
	(let [
		keywords (clojure.string/join ", " (get body "sm_api_keyword_array"))
		content (get body "sm_api_content")
		]
	(structure-tldr-response keywords content)
	)

	
)


(defn handle-tldr [request]
	(let [
		article (get-in request [:body :item :message :message])
		]
		(let [options {:form-params {:sm_api_input article }}
      		{:keys [status body error]} @(http/post "http://api.smmry.com/?SM_API_KEY=2573B09504&SM_KEYWORD_COUNT=4" options)]
		  (if error
		    (structure-error-response error)
		    (arrange-tldr (json/read-str body))	
		  )
		)
	)
)
