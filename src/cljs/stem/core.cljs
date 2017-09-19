(ns stem.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [think.semantic-ui :as ui]))

;; -------------------------
;; Views

(def nav
  [ui/menu {:color :blue :inverted true :pointing true :borderless true}
   [ui/menu-menu {:position :right}
    [ui/menu-item {:position :right} [:a {:href "/"} "Home"]]
    [ui/menu-item {:position :right} [:a {:href "/speaker"} "Speaker"]]
    [ui/menu-item {:position :right} [:a {:href "/audience"} "Audience"]]]])

(defn home-page []
  [:div [ui/header {:as "h3"} "Welcome"]])

(defn speaker-page []
  [:div [:h2 "About stem"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn audience-page []
  [:div [:h2 "About stem"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div.page
   nav
   [:div.content-section
    [(session/get :current-page)]]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/speaker" []
  (session/put! :current-page #'speaker-page))

(secretary/defroute "/audience" []
  (session/put! :current-page #'audience-page))

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
