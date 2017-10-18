(ns stem.pages.audience
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [think.semantic-ui :as ui]))

(def username*
  (atom nil))

(def all-data*
  (atom nil))

(defn handler [response]
  (do
    (reset! all-data* response)
    (.log js/console (str "all data received"))))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn read-data []
  (GET "/read-data" {:handler handler
                     :error-handler error-handler}))

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
    [ui/button {:disabled (empty? @username*)} option1]
    [ui/button-or]
    [ui/button {:disabled (nil? @username*)} option2]]])

(defn slider
  [name option1 option2]
  [:div.slider
   [ui/header {:size "medium"} name]
   [:input {:disabled (empty? @username*)
            :type "range"
            :min 0
            :max 100
            :style {:width "95%"} :class "mdl-slider mdl-js-slider"}]
   [:div.labels {:style {:height "20px" :position :relative}}]
   [:div {:style {:float "left" :color "grey"}} option1]
   [:div {:style {:float "right" :color "grey"}} option2]])

(defn feedback
  [name option1 option2]
  [:div.feedback
   [ui/header {:size "medium"} name]
   [ui/form
    [ui/text-area {:disabled (empty? @username*)
                   :placeholder option1
                   :autoHeight true}]
    [ui/button {:primary true :style {:margin "10px 0"} :href "#"} "Submit Feedback"]]])

(defn response-module
  [i {:keys [datatype name option1 option2 avg votes]}]
  (let [datatypes {:feedback "Open Feedback"
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
  [:div.audience-page
   [ui/header {:size "large"} "Audience"]
   [:div.content-section
    [user-info]
    (doall
      (map-indexed
        (fn [i element]
          (response-module i element))
        @all-data*))]])
