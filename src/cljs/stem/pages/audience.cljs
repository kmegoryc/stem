(ns stem.pages.audience
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [stem.data :refer [all-surveys* read-surveys update-survey-handler error-handler]]
            [think.semantic-ui :as ui]))

(def username*
  (atom nil))

(def anonymous-toggle*
  (atom true))

(defn user-info []
  [:div.user-info
   [ui/input {:focus true
              :placeholder "Username..."
              :on-change (fn [ev data]
                           (reset! username* (:value (js->clj data :keywordize-keys true))))}]
   (if-not (empty? @username*)
     [ui/header {:color "teal" :size "medium"} (str "Welcome, " @username* "!")])])

(defn toggle
  [name option1 option2]
  [:div.toggle
   [ui/header {:size "medium"} name]
   [ui/button-group
    [ui/button {:disabled (empty? @username*)
                :on-click (fn [ev]
                            (POST "/update-survey"
                                  {:params {:id @username* :name name :choice 0}
                                   :handler update-survey-handler
                                   :error-handler error-handler}))} option1]
    [ui/button-or]
    [ui/button {:disabled (nil? @username*)
                :on-click (fn [ev]
                            (POST "/update-survey"
                                  {:params {:id @username* :name name :choice 100}
                                   :handler update-survey-handler
                                   :error-handler error-handler}))} option2]]])

(defn slider
  [name option1 option2]
  [:div.slide
   [ui/header {:size "medium"} name]
   [:div {:id "slidecontainer"}
    [:input {:class "slider" :id "myRange"
             :disabled (empty? @username*)
             :type "range"
             :min 0 :max 100
             :on-change (fn [ev data]
                          (let [value (.-target.value ev)]
                            (POST "/update-survey"
                                  {:params {:id @username* :name name :choice value}
                                   :handler update-survey-handler
                                   :error-handler error-handler})))}]]
   [:div.labels {:style {:height "20px" :position :relative}}
    [:div {:style {:float "left" :color "grey"}} option1]
    [:div {:style {:float "right" :color "grey"}} option2]]])

(defn feedback
  [name option1 option2]
  (let [feedback* (atom nil)]
    [:div.feedback
     [ui/header {:size "medium"} name]
     [ui/form
      [ui/text-area {:disabled (empty? @username*)
                     :placeholder option1
                     :autoHeight true
                     :on-change (fn [ev data]
                                  (reset! feedback* (:value (js->clj data :keywordize-keys true))))}]
      [ui/button {:primary true
                  :style {:margin "10px 0"}
                  :href "#"
                  :on-click (fn [ev]
                              (POST "/update-survey"
                                    {:params {:id @username* :name name :choice @feedback* :anonymous toggle}
                                     :handler update-survey-handler
                                     :error-handler error-handler}))} "Submit Feedback"]]]))

(defn response-module
  [{:keys [datatype name option1 option2 avg votes]}]
  (let [datatypes {:feedback "Question"
                   :slider "Slider"
                   :toggle "Toggle"}]
    ^{:key (random-uuid)}
    [:div.response-module
     (cond (= datatype (datatypes :toggle))
           (toggle name option1 option2)
           (= datatype (datatypes :slider))
           (slider name option1 option2)
           (= datatype (datatypes :feedback))
           (feedback name option1 option2)
           :else
           [:div.error "Error rendering components."])]))

(defn anonymous-mode []
  (fn []
    [:div.anonymous-label {:style {:float "right"}}
     [ui/label {:color :teal
                :style {:cursor :pointer}
                :on-click (fn [ev data]
                            (reset! anonymous-toggle* (not @anonymous-toggle*))
                            (println @anonymous-toggle*))}
      [ui/icon {:name (if @anonymous-toggle* "toggle on" "toggle off")}]
      (str "Anonymous Mode " (if @anonymous-toggle* "ON" "OFF"))]]))

(read-surveys)

(defn audience-page []
  (fn []
    [:div.audience-page
     [anonymous-mode]
     [:div.content-section
      [user-info]
      (doall
        (map
          (fn [element]
            (response-module element))
          @all-surveys*))]]))
