(ns stem.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [stem.pages.home :as home]
            [stem.pages.audience :as audience]
            [stem.pages.speaker :as speaker]
            [think.semantic-ui :as ui]))

;; -------------------------
;; Views

(defn current-page []
  [:div.wrapper
   [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home/home-page))

(secretary/defroute "/teacher" []
  (session/put! :current-page #'speaker/speaker-page))

(secretary/defroute "/student" []
  (session/put! :current-page #'audience/audience-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
