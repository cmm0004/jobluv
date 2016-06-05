(ns jobluv.handler
  (:require [compojure.core :refer :all]
  			[compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))
(defn handle-hipchat-message [message]
	(response message))

(defroutes app-routes
  (GET "/PING" [] "PONG")
  (POST "/" {body :body} (handle-hipchat-message body))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
   	  (wrap-json-body)
      (wrap-json-response)))
