(ns stem.handler
  (:require [clojure.edn :as edn]
            [config.core :refer [env]]
            [compojure.core :refer [GET POST PUT defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [stem.middleware :refer [wrap-middleware]]
            [ring.util.response :refer [redirect content-type resource-response response status]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css  "//cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.2/semantic.min.css"
                 "https://code.getmdl.io/1.3.0/material.indigo-pink.min.css"
                 (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js"
                 "https://code.getmdl.io/1.3.0/material.min.js")]))

(defn read-data []
  {:status 200
   :body (edn/read-string (slurp "modules.edn"))})

(defn add-module
  [request]
  (let [request-fp (assoc (:params request) :votes [])
        conj-result (conj (edn/read-string (slurp "modules.edn")) request-fp)]
    (spit "modules.edn" conj-result))
  {:status 200
   :body "add module successful"})

(defn remove-module
  [request]
  (let [request-fp (:params request)
        name-to-remove (:name request-fp)
        file (edn/read-string (slurp "modules.edn"))
        new-file (remove (fn [module]
                           (= name-to-remove (:name module))) file)]
    (spit "modules.edn" (pr-str new-file))
    (prn (str "remove request: " request-fp))
    (println "file: " file)
    (spit "modules.edn" (pr-str new-file))
    {:status 200
     :body "remove successful"}))

(defn update-module
  [request]
  (let [request-fp (:params request)
        file-data (vec (edn/read-string (slurp "modules.edn")))
        module-index (first (remove nil? (map-indexed (fn [i {:keys [name]}]
                                                        (if (= (:name request-fp) name)
                                                          i)) file-data)))
        vote-data (get-in file-data [module-index :votes])
        vote-index (first (remove nil? (map-indexed (fn [i {:keys [id]}]
                                                      (if (= (:id request-fp) id)
                                                        i)) vote-data)))
        new-data (if-not (nil? vote-index)
                   ;;if vote index is not nil, then update-in conj :votes with new request
                   (assoc-in file-data [module-index :votes vote-index] request-fp)
                   (update-in file-data [module-index :votes] conj request-fp))]
    (spit "modules.edn" (pr-str new-data)))
  {:status 200
   :body "update successful"})

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/speaker" [] (loading-page))
  (GET "/audience" [] (loading-page))
  (POST "/add-module" request (add-module request))
  (POST "/remove-module" request (remove-module request))
  (POST "/update-module" request (update-module request))
  (GET "/read-data" [] (read-data))

  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
