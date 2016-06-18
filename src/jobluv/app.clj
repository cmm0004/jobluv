(ns jobluv.app
  (:gen-class)
  (:require [compojure.core :refer :all]
  			[compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.adapter.jetty :as jetty]  
            [jobluv.handlers.jobluv :as jobluvhandler]
            [jobluv.handlers.tldr :as tldrhandler]         
       
           )
  )

(defroutes app-routes
  (GET "/PING" [] "PONG")
  (POST "/" request 
  	(jobluvhandler/handle-hipchat-message request)
  )	
  (POST "/tldr" request
  	(tldrhandler/handle-tldr request)
  	)
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
   	  (wrap-json-body {:keywords? true})
      (wrap-json-response)))

(defn -main [] 
	jetty/run-jetty app)
