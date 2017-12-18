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

(defn nav []
  [ui/segment {:inverted true
               :style {:padding "10px 5px"
                       :background "#1c4869"
                       :letter-spacing "1.5px"}}
   [ui/menu {:inverted true :secondary true}
    [ui/menu-item {:style {:padding "0 0 0 20px"}}
     [ui/image {:size "mini"
                :src "/images/logo_white.png"}]]
    [ui/menu-menu {:position :right}
     [ui/menu-item {:position :right}
      [:a {:href "/"} "HOME"]]
     [ui/menu-item {:position :right}
      [:a {:href "/speaker"} "SPEAKER"]]
     [ui/menu-item {:position :right}
      [:a {:href "/audience"} "AUDIENCE"]]]]])


(defn current-page []
  [:div.wrapper
   [nav]
   [:div.page-content
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
