(ns stem.pages.audience
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [stem.data :refer [all-data* read-data post-data-handler error-handler]]
            [think.semantic-ui :as ui]))

(def username*
  (atom nil))

(defn user-info []
  [:div.user-info
   [ui/input {:focus true
              :placeholder "Username..."
              :on-change (fn [ev data]
                           (reset! username* (:value (js->clj data :keywordize-keys true))))}]
   (if-not (empty? @username*)
     [ui/header {:color "teal" :size "medium"} (str "Welcome, " @username* "!")]
     [ui/header {:color "teal" :size "medium"} (str "Please enter your username above to enable feedback submissions.")])])

(defn toggle
  [name option1 option2]
  [:div.toggle
   [ui/header {:size "medium"} name]
   [ui/button-group
    [ui/button {:disabled (empty? @username*)
                :on-click (fn [ev]
                            (POST "/update-module"
                                  {:params {:id @username* :name name :choice 0}
                                   :handler post-data-handler
                                   :error-handler error-handler})
                            (read-data))} option1]
    [ui/button-or]
    [ui/button {:disabled (nil? @username*)
                :on-click (fn [ev]
                            (POST "/update-module"
                                  {:params {:id @username* :name name :choice 100}
                                   :handler post-data-handler
                                   :error-handler error-handler})
                            (read-data))} option2]]])

(defn slider
  [name option1 option2]
  [:div.slider
   [ui/header {:size "medium"} name]
   [:input {:class "mdl-slider mdl-js-slider"
            :disabled (empty? @username*)
            :type "range"
            :min 0
            :max 100
            :style {:width "95%"}
            :on-change (fn [ev data]
                         (let [value (.-target.value ev)]
                           (POST "/update-module"
                                 {:params {:id @username* :name name :choice value}
                                  :handler post-data-handler
                                  :error-handler error-handler})
                           (read-data)))}]
   [:div.labels {:style {:height "20px" :position :relative}}]
   [:div {:style {:float "left" :color "grey"}} option1]
   [:div {:style {:float "right" :color "grey"}} option2]])

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
                              (POST "/update-module"
                                    {:params {:id @username* :name name :choice @feedback*}
                                     :handler post-data-handler
                                     :error-handler error-handler})
                              (read-data))} "Submit Feedback"]]]))

(defn response-module
  [i {:keys [datatype name option1 option2 avg votes]}]
  (let [datatypes {:feedback "Question"
                   :slider "Slider"
                   :toggle "Toggle"}]
    ^{:key i}
    [:div.response-module
     (cond (= datatype (datatypes :toggle))
           (toggle name option1 option2)
           (= datatype (datatypes :slider))
           (slider name option1 option2)
           (= datatype (datatypes :feedback))
           (feedback name option1 option2)
           :else
           [:div.error "Error rendering components."])]))

(read-data)

(defn audience-page []
  (fn []
    [:div.audience-page
     [ui/header {:size "large"} "Audience"]
     [:div.content-section
      [user-info]
      (doall
        (map-indexed
          (fn [i element]
            (response-module i element))
          @all-data*))]]))
