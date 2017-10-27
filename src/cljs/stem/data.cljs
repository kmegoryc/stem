(ns stem.data
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST PUT]]))

(def all-data*
  (atom nil))

(defn post-data-handler [response]
  (.log js/console (str "add or remove successful")))

(defn read-data-handler [response]
  (do
    (reset! all-data* response)
    (.log js/console (str "all data: " response))))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn read-data []
  (GET "/read-data" {:handler read-data-handler
                     :error-handler error-handler}))
