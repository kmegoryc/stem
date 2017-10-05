(ns stem.pages.audience
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [think.semantic-ui :as ui]))

(defn audience-page []
  [:div.audience-page
   [:div.submissions
    [ui/header {:size "large"} "Submit Responses"]]])
