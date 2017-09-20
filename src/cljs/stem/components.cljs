(ns stem.components
  (:require [think.semantic-ui :as ui]))

(defn button
  [name option1 option2]
  [:div
   [ui/header {:size "medium"} name]
   [ui/button-group
    [ui/button option1]
    [ui/button-or]
    [ui/button option2]]])

(defn slider
  [name option1 option2]
  [:div
   [ui/header {:size "medium"} name]
   [:input {:min 0
            :max 20
            :type "range"
            :defaultValue 10}]])

(defn poll
  [name option1 option2]
  [:div
   [ui/header {:size "medium"} name]])

(defn open-feedback
  [name option1]
  [:div
   [ui/header {:size "medium"} name]])
