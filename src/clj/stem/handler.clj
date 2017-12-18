(ns stem.handler
  (:require [clojure.java.io :as io]
            [config.core :refer [env]]
            [compojure.core :refer [GET POST PUT defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [stem.middleware :refer [wrap-middleware]]
            [ring.util.response :refer [redirect content-type resource-response response status]]
            [stem.model :refer [create-survey remove-survey update-survey read-surveys]]))


(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "google-signin-client_id"
           :content "430298023856-oqov216tqs012mqlfurvqkup9eai9k7h.apps.googleusercontent.com"}]
   [:script {:src "https://apis.google.com/js/platform.js"
             :async true
             :defer true}]
   (include-css  "//cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.2/semantic.min.css"
                 "/css/slider.css"
                 "https://fonts.googleapis.com/css?family=Roboto"
                 "https://fonts.googleapis.com/css?family=Open+Sans"
                 (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js"
                 "https://code.getmdl.io/1.3.0/material.min.js")]))

#_(let [conn (mongo/connect {:host "127.0.0.1" :port 27017})
        db (mongo/get-db conn "monger-test")
        coll "documents"]
    (coll/insert db coll {:first_name "John"  :last_name "Lennon"})
    (coll/insert db coll {:first_name "Ringo" :last_name "Starr"})
    (coll/insert db coll {:first_name "Keren" :last_name "Megory"})
    (clojure.pprint/pprint (coll/find-maps db coll {:first_name "John"})))

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/speaker" [] (loading-page))
  (GET "/audience" [] (loading-page))
  (POST "/add-survey" request (create-survey request))
  (POST "/remove-survey" request (remove-survey request))
  (POST "/update-survey" request (update-survey request))
  (GET "/read-surveys" [] (read-surveys))

  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
