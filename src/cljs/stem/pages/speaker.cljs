(ns stem.pages.speaker
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST PUT]]
            [think.semantic-ui :as ui]
            [stem.data :refer [all-data* read-data post-data-handler error-handler]]))

(defn create-module []
  (let [create* (atom {:datatype "Choose a Module"
                       :name nil
                       :option1 nil
                       :option2 nil
                       :avg 50})
        module-options ["Slider" "Toggle" "Open Feedback"]]
    (fn []
      [:div.create-module
       [ui/modal {:trigger (reagent/as-element [ui/button {:primary true} "Create New Module"])}
        [ui/modal-header "Create a New Module"]
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
            [ui/form-input {:placeholder "Name (Ex: Pace)"
                            :on-change (fn [ev data]
                                         (swap! create* assoc :name (:value (js->clj data :keywordize-keys true))))}]]]
          (when (contains? (into #{} module-options) (@create* :datatype))
            [:div.module-options
             [ui/form-group
              [ui/form-input {:label "Option 1" :placeholder "Ex: Too Slow"
                              :on-change (fn [ev data]
                                           (swap! create* assoc :option1 (:value (js->clj data :keywordize-keys true))))}]
              [ui/form-input {:label "Option 2" :placeholder "Ex: Too Fast"
                              :on-change (fn [ev data]
                                           (swap! create* assoc :option2 (:value (js->clj data :keywordize-keys true))))}]]
             [ui/button {:primary true
                         :on-click (fn [ev]
                                     (POST "/add-module"
                                           {:params @create*
                                            :handler post-data-handler
                                            :error-handler error-handler})
                                     (read-data))} "Submit"]])]]]])))

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
      (fn [j {:keys [id name choice]}]
        ^{:key (random-uuid)}
        [ui/feed-event
         [ui/feed-label {:image "http://www.infragistics.com/media/8948/anonymous_200.gif"}]
         [ui/feed-content
          [ui/feed-summary (str id " posted a comment.")]
          [ui/feed-extra {:text true} choice]]]) votes)]])

(defn results-module
  [i {:keys [datatype name option1 option2 avg votes]}]
  (let [datatypes {:feedback "Open Feedback"
                   :slider "Slider"
                   :toggle "Toggle"}]
    ^{:key i}
    [:div.results-module
     [ui/header {:size "medium"} option1]
     [ui/button {:primary true
                 :icon true
                 :circular true
                 :style {:position :absolute :top 0 :right 0 :margin "10px"}
                 :on-click (fn [ev]
                             (POST "/remove-module"
                                   {:params {:name name}
                                    :handler post-data-handler
                                    :error-handler error-handler})
                             (read-data))}
      [ui/icon {:name "remove"}]]
     (if (= datatype (datatypes :feedback))
       (feed votes)
       (progress avg votes option1 option2))]))

(read-data)

(defn speaker-page []
  (fn []
    [:div.speaker-page
     [ui/header {:size "large"} "Speaker"]
     [:div.content-section
      [create-module]
      (map-indexed
        (fn [i element]
          (results-module i element))
        @all-data*)]]))
