(ns stem.styles.core
  (:require [garden.def :refer [defstyles]]
            [garden.color :as color :refer [hsl rgb rgba]]
            [garden.units :refer [px em percent]]
            [greenhouse.grid :refer [column span clearfix center stack align on cycle-props]]))

(def module-styles
  {:position :relative
   :padding (px 40)
   :border-radius (px 5)
   :border "1px solid lightgrey"
   :margin [[(px 20) 0]]
   :box-shadow [[0 0 (px 15) (rgba 0 0 0 0.2)]]})

(def results-module
  [:div.results-module
   module-styles])

(def response-module
  [:div.response-module
   module-styles])

(def variables
  {:max-width (px 1200)
   :max-width-tablet (px 950)})

(def section
  (list {:width (percent 100)
         :padding (px 30)}
        (on :tablet [:&
                     (center :max-width (:max-width-tablet variables)
                             :pad 30)])
        (on :laptop [:&
                     (center :max-width (:max-width variables)
                             :pad 30)])))

(defstyles styles
  [:div.wrapper
   [:.segment {:border-radius 0}]
   [:div.page-content
    section
    [:div.content-section
     response-module]]])
