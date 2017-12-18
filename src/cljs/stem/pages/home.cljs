(ns stem.pages.home
  (:require [think.semantic-ui :as ui]))

(defn success
  [googleUser]
  (prn "got in success!")
  (let [profile (.getBasicProfile googleUser)
        id (.getId profile)]
    (println (str "ID: " id))))

(defn home-page []
  [:div.home-page
   [:div {:class "g-signin2"
          :data-onsuccess (fn [googleUser]
                            (println "got to on success!"))} "Google"]])
