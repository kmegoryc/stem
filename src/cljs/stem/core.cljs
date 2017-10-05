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

(def username*
  (atom nil))

(def nav
  [ui/menu {:inverted true :pointing true :borderless true}
   [ui/menu-item
    [ui/image {:src "http://www.guruadvisor.net/images/numero11/cloud.png" :height 32}]]
   [ui/menu-menu {:position :right}
    [ui/menu-item {:position :right} [:a {:href "/"} "Home"]]
    [ui/menu-item {:position :right} [:a {:href "/speaker"} "Speaker"]]
    [ui/menu-item {:position :right} [:a {:href "/audience"} "Audience"]]
    [ui/menu-item {:position :right}
     [ui/input {:focus true :inverted true :placeholder "Username" :style {:margin-right "10px"}
                :on-change (fn [ev data]
                             (reset! username* (:value (js->clj data :keywordize-keys true))))}]]]])


(defn current-page []
  [:div.page
   nav
   [:div.content-section
    [(session/get :current-page)]]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home/home-page))

(secretary/defroute "/speaker" []
  (session/put! :current-page #'speaker/speaker-page))

(secretary/defroute "/audience" []
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
