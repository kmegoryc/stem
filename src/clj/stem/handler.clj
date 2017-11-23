(ns stem.handler
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [config.core :refer [env]]
            [compojure.core :refer [GET POST PUT defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [stem.middleware :refer [wrap-middleware]]
            [ring.util.response :refer [redirect content-type resource-response response status]]
            [resource-seq.core :refer [resource-seq]]
            [clojure.java.io :as io]))

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
                 "/css/slider.css"
                 (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js"
                 "https://code.getmdl.io/1.3.0/material.min.js")]))

(defn read-edn []
  (->> (resource-seq)
    (filter #(and (.contains (first %) "public/data") (.contains (first %) ".edn")))
    (map (fn [[path reader-fn]]
           (with-open [r (reader-fn)] (edn/read-string (slurp r)))))
    (first)))

(defn write-edn [edn-updated]
  (spit "resources/public/data/modules.edn" edn-updated))

(defn read-data []
  (response (read-edn)))

(defn add-module
  [request]
  (let [request-fp (assoc (:params request) :votes [])
        conj-result (conj (read-edn) request-fp)]
    (write-edn conj-result))
  (response "add module successful"))

(defn remove-module
  [request]
  (let [request-fp (:params request)
        name-to-remove (:name request-fp)
        file (read-edn)
        new-file (remove (fn [module]
                           (= name-to-remove (:name module))) file)]
    (write-edn (pr-str new-file))
    (response "remove successful")))

(defn update-module
  [request]
  (let [request-fp (:params request)
        file-data (vec (read-edn))
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
    (write-edn (pr-str new-data)))
  (response "update successful"))

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
