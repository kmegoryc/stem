(ns stem.data
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST PUT]]))

(def all-data*
  (atom nil))

(defn read-data-handler [response]
  (do
    (reset! all-data* response)
    (.log js/console (str "all data: " response))))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn read-data []
  (GET "/read-data" {:handler read-data-handler
                     :error-handler error-handler}))

(defn update-module-handler []
  (do
    (read-data)
    (.log js/console (str "update successful"))))

(defn add-module-handler []
  (do
    (read-data)
    (.log js/console (str "add successful"))))

(defn remove-module-handler []
  (do
    (read-data)
    (.log js/console (str "remove successful"))))

#_(add-watch all-data* :watcher
             (fn [key atom old-state new-state]
               (prn "-- Atom Changed --")
               (prn "key" key)
               (prn "atom" atom)
               (prn "old-state" old-state)
               (prn "new-state" new-state)))
