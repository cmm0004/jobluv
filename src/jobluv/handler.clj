(ns jobluv.handler
  (:require [compojure.core :refer :all]
  			[clojure.string :as str]
  			[compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.java.jdbc :as j]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.adapter.jetty :as jetty]
            [jobluv.links :as links]
            [environ.core :refer [env]]))

(def db { 	:classname "org.postgresql.Driver"
			:subprotocol "postgresql"
          	:subname (env :database-url)
            :user (env :database-user)
            :password (env :database-password)})


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

(defn query-jobluvs [usermention]
	(get-in 
		(first 
			(j/query db ["SELECT jobluv_amount FROM jobluvs where hipchat_username = ?" usermention])
		)
		[:jobluv_amount]
	)
)

(defn exec-inc-jobluvs [usermention]
	(j/execute! db ["UPDATE jobluvs SET jobluv_amount = jobluv_amount + 1 WHERE hipchat_username = ?" usermention])
)

(defn exec-dec-jobluvs [usermention]
	(j/execute! db ["UPDATE jobluvs SET jobluv_amount = jobluv_amount - 1 WHERE hipchat_username = ?" usermention])
)

(defn inc-dec-key [usermention should_increment]
	(if should_increment
		(exec-inc-jobluvs usermention)
		(exec-dec-jobluvs usermention)
	)
)

(defn plusplus [usermention]
	(inc-dec-key usermention true)
	(structure-response usermention (rand-nth (links/get_plus_jobluv_links)))
)

(defn minusminus [usermention]
	(inc-dec-key usermention nil)
	(structure-response usermention (rand-nth (links/get_minus_jobluv_links)))
)

(defn build-key-string [jobluv_amount]
	(if (= jobluv_amount nil)
		(build-key-string 0)
		(format " is major %s" (str/join " " (repeat jobluv_amount "(key)")))
	)
)

(defn question [usermention]
	(structure-response usermention 
						(build-key-string 
							(query-jobluvs usermention)
						)
	)
)

(defn add-user [usermention]
	 (j/execute! db ["INSERT INTO jobluvs(hipchat_username, jobluv_amount, is_the_job_don) VALUES (?, 1, false);" usermention])		
	 (structure-response usermention " You are now on the road to success, let's win more.")
)

(defn handle-hipchat-message [request]
	(let [
		command (last (str/split (get-in request [:body :item :message :message]) #" "))
		usermention (get-in (first (get-in request [:body :item :message :mentions])) [:mention_name])
		from (get-in (first (get-in request [:body :item :message :from])) [:mention_name])
		]
		(if (=  (env :they) (str/lower-case from))
			(structure-response usermention "I don't listen to THEY")
			(case command
				"++" (plusplus usermention)
				"--" (minusminus usermention)
				"?"  (question usermention)
				"init" (add-user usermention)
				(structure-response usermention "I need to know what you want")
			)
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
