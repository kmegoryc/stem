(ns stem.pages.speaker
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST PUT]]
            [think.semantic-ui :as ui]
            [stem.data :refer [all-surveys* read-surveys add-survey-handler remove-survey-handler error-handler]]))

(defn create-module []
  (let [create* (atom {:datatype "Choose a Module"
                       :name nil
                       :option1 nil
                       :option2 nil
                       :avg 50})
        module-options ["Slider" "Toggle" "Question"]]
    (fn []
      [:div.create-module
       [ui/modal {:trigger (reagent/as-element
                             [ui/button {:primary true
                                         :icon "plus"
                                         :labelPosition :left
                                         :content "New Survey"}])}
        [ui/modal-header "Create a New Survey"]
        [ui/modal-content
         [ui/form
          [:div.module-type
           [ui/form-group
            [ui/form-input
             [ui/dropdown {:floating true
                           :labeled true
                           :button true
                           :icon "dropdown"
                           :class "icon"
                           :options (map
                                      (fn [module-option]
                                        {:text module-option
                                         :value module-option})
                                      module-options)
                           :on-change (fn [ev data]
                                        (swap! create* assoc :datatype (:value (js->clj data :keywordize-keys true))))
                           :text (@create* :datatype)}]]
            [ui/form-input {:placeholder (cond
                                           (= (@create* :datatype) "Question")
                                           "Ex: Do you have any questions about that concept?"
                                           :else
                                           "Ex: How's my pace?")
                            :on-change (fn [ev data]
                                         (swap! create* assoc :name (:value (js->clj data :keywordize-keys true))))}]]]
          (when (contains? (into #{} module-options) (@create* :datatype))
            [:div.module-options-and-submit
             (when-not (= (@create* :datatype) "Question")
               [:div.module-options
                [ui/form-group
                 [ui/form-input {:label "Option 1" :placeholder "Ex: Too Slow"
                                 :on-change (fn [ev data]
                                              (swap! create* assoc :option1 (:value (js->clj data :keywordize-keys true))))}]
                 [ui/form-input {:label "Option 2" :placeholder "Ex: Too Fast"
                                 :on-change (fn [ev data]
                                              (swap! create* assoc :option2 (:value (js->clj data :keywordize-keys true))))}]]])
             [ui/button {:primary true
                         :on-click (fn [ev]
                                     (POST "/add-survey"
                                           {:params @create*
                                            :handler add-survey-handler
                                            :error-handler error-handler}))} "Submit"]])]]]])))

(defn progress
  [avg votes option1 option2]
  [:div.progress
   [ui/progress {:percent (if (empty? votes)
                            avg
                            (/ (apply + (map #(js/parseInt (:choice %)) votes)) (count votes)))
                 :indicating true
                 :progress true
                 :color (cond
                          (> (if (empty? votes)
                               avg
                               (/ (apply + (map #(js/parseInt (:choice %)) votes)) (count votes))) 75) "red"
                          (< (if (empty? votes)
                               avg
                               (/ (apply + (map #(js/parseInt (:choice %)) votes)) (count votes))) 25) "red"
                          :else "olive")}]
   [:div {:style {:float "left" :color "grey"}} option1]
   [:div {:style {:float "right" :color "grey"}} option2]])

(defn feed
  [votes]
  [:div.feed
   [ui/feed {:style {:margin "20px 0"}}
    (map-indexed
      (fn [j {:keys [id name choice anonymous]}]
        ^{:key (random-uuid)}
        [ui/feed-event
         [ui/feed-label {:image "http://www.infragistics.com/media/8948/anonymous_200.gif"}]
         [ui/feed-content
          [ui/feed-summary (if anonymous "Anonymous posted a comment." (str id " posted a comment."))]
          [ui/feed-extra {:text true} choice]]]) votes)]])

(defn results-module
  [i {:keys [datatype name option1 option2 avg votes]}]
  (let [datatypes {:feedback "Question"
                   :slider "Slider"
                   :toggle "Toggle"}]
    ^{:key i}
    [:div.results-module
     [ui/header {:size "medium"} name]
     [ui/button {:primary true
                 :icon true
                 :circular true
                 :style {:position :absolute :top 0 :right 0 :margin "10px"}
                 :on-click (fn [ev]
                             (POST "/remove-survey"
                                   {:params {:name name}
                                    :handler remove-survey-handler
                                    :error-handler error-handler}))}
      [ui/icon {:name "remove"}]]
     (if (= datatype (datatypes :feedback))
       (feed votes)
       (progress avg votes option1 option2))]))

(read-surveys)

(defn speaker-page []
  (fn []
    [:div.speaker-page
     [:div.content-section
      [create-module]
      (map-indexed
        (fn [i element]
          (results-module i element))
        @all-surveys*)]]))
