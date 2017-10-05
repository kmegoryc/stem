(ns stem.styles.core
  (:require [garden.def :refer [defstyles]]
            [garden.units :refer [px em percent]]
            [greenhouse.grid :refer [column span clearfix center stack align on cycle-props]]))

(def module
  [:div.module {:padding (px 40)
                :border-radius (px 5)
                :border "1px solid lightgrey"
                :margin [[(px 20) 0]]}
   [:div.teacher-header {:margin-top (px 10)}]])

(def variables
  {:max-width (px 1200)
   :max-width-tablet (px 950)})

(def content-section
  (list {:width (percent 100)
         :padding (px 30)}
        (on :tablet [:&
                     (center :max-width (:max-width-tablet variables)
                             :pad 30)])
        (on :laptop [:&
                     (center :max-width (:max-width variables)
                             :pad 30)])))

(defstyles styles
  [:div.page
   [:.menu {:border-radius 0}]
   [:div.content-section
    content-section
    module]])
