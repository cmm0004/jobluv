(ns jobluv.handler
  (:require [compojure.core :refer :all]
  			[clojure.string :as str]
  			[compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.java.jdbc :as j]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [jobluv.links :as links]))

(def db { 	:classname "org.postgresql.Driver"
			:subprotocol "postgresql"
          	:subname (System/getenv "PG_SUBNAME")
            :user (System/getenv "PG_USER")
            :password (System/getenv "PG_PASSWORD")})

(defn structure-response [usermention linkstring]
 	(response 
 		{
	 		:color "green"
	 		:message (format "@%s %s" usermention linkstring)
	 		:notify false
	 		:message_format "text"
 		}
 	)
)

(defn handle-hipchat-message [request]
	(let [
		command (last (str/split (get-in request [:body :item :message :message]) #" "))
		usermention (get-in (first (get-in request [:body :item :message :mentions])) [:mention_name])
		]
		(case command
			"++" (structure-response usermention (rand-nth (links/get_plus_jobluv_links)))
			"--" (structure-response usermention (rand-nth (links/get_minus_jobluv_links)))
			"?"  (prn (get-in (first (j/query db ["SELECT jobluv_amount FROM jobluvs where hipchat_username = ?" usermention]))[:jobluv_amount]))
			(structure-response usermention "i need to know what you want")
		)
	)
)

(defroutes app-routes
  (GET "/PING" [] "PONG")
  (POST "/" request 
  	(handle-hipchat-message request)
  )	
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
   	  (wrap-json-body {:keywords? true})
      (wrap-json-response)))
