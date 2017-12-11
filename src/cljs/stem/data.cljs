(ns stem.data
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST PUT]]))

(def all-surveys*
  (atom nil))

(defn read-surveys-handler [response]
  (do
    (reset! all-surveys* response)
    (.log js/console (str "all surveys: " response))))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn read-surveys []
  (GET "/read-surveys" {:handler read-surveys-handler
                        :error-handler error-handler}))

(defn update-survey-handler []
  (do
    (read-surveys)
    (.log js/console (str "update successful"))))

(defn add-survey-handler []
  (do
    (read-surveys)
    (.log js/console (str "add successful"))))

(defn remove-survey-handler []
  (do
    (read-surveys)
    (.log js/console (str "remove successful"))))

#_(add-watch all-data* :watcher
             (fn [key atom old-state new-state]
               (prn "-- Atom Changed --")
               (prn "key" key)
               (prn "atom" atom)
               (prn "old-state" old-state)
               (prn "new-state" new-state)))
