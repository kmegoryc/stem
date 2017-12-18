(ns stem.pages.speaker
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST PUT]]
            [think.semantic-ui :as ui]
            [cljs-time.core :as t]
            [cljs-time.local :as l]
            [cljs-time.format :as f :refer [formatters formatter]]
            [stem.data :refer [all-surveys* read-surveys add-survey-handler remove-survey-handler error-handler]]))

(def custom-formatter (formatter "H:mA MMM dd"))

(defn create-module []
  (let [create* (atom {:datatype "Survey Type"
                       :name nil
                       :option1 nil
                       :option2 nil
                       :avg 50
                       :timestamp nil
                       :timestamp-verbose nil
                       :importance 100})
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
             [:b "Priority"]
             [:div.rating
              [ui/rating {:maxRating 5
                          :clearable true
                          :onRate (fn [ev data]
                                    (swap! create* assoc :importance (:rating (js->clj data :keywordize-keys true))))}]]])]]
        (when (contains? (into #{} module-options) (@create* :datatype))
          [ui/modal-actions
           [ui/button {:primary true
                       :on-click (fn [ev]
                                   (swap! create* assoc :timestamp (f/unparse custom-formatter (t/local-date-time (t/now))))
                                   (swap! create* assoc :timestamp-verbose (str (t/local-date-time (t/now))))
                                   (POST "/add-survey"
                                         {:params @create*
                                          :handler add-survey-handler
                                          :error-handler error-handler}))} "Submit"]])]])))

(defn progress
  [avg votes option1 option2]
  [:div.progress
   [ui/progress {:style {:margin "20px 0"}
                 :percent (if (empty? votes)
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
                          :else "olive")}]])

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
  [i {:keys [datatype name option1 option2 avg votes timestamp importance]}]
  (let [datatypes {:feedback "Question"
                   :slider "Slider"
                   :toggle "Toggle"}
        component (if (= datatype (datatypes :feedback))
                    (if-not (= (count votes) 0) (feed votes) [:i "Waiting for responses..."])
                    (progress avg votes option1 option2))
        component-modal [ui/modal {:trigger (reagent/as-element
                                              [ui/icon {:name "external"
                                                        :style {:cursor :pointer}
                                                        :color :blue}])}
                         [ui/modal-header name]
                         [ui/modal-content {:style {:margin "0 0 20px 0"}}
                          [:p (str "Total Votes: " (count votes))]
                          component
                          [:div {:style {:float "left" :color "grey"}} option1]
                          [:div {:style {:float "right" :color "grey"}} option2]]]]

    ^{:key i}
    [ui/table-row
     [ui/table-cell {:textAlign :center}
      [ui/button {:basic true
                  :primary true
                  :icon true
                  :circular true
                  :on-click (fn [ev]
                              (POST "/remove-survey"
                                    {:params {:name name}
                                     :handler remove-survey-handler
                                     :error-handler error-handler}))}
       [ui/icon {:name "remove"}]]]
     [ui/table-cell
      [ui/item-header name]]
     [ui/table-cell {:width :six}
      component]
     [ui/table-cell {:verticalAlign :center}
      component-modal]
     [ui/table-cell {:style {:color :grey
                             :font-size "13px"}}
      timestamp]
     [ui/table-cell
      [ui/rating {:maxRating 5
                  :disabled true
                  :rating importance}]]]))

(read-surveys)

(defn speaker-page []
  (let [organize-by* (atom "timestamp")]
    (fn []
      (let [table-header [ui/table-header
                          [ui/table-row
                           [ui/table-header-cell]
                           [ui/table-header-cell "Survey"]
                           [ui/table-header-cell "Status"]
                           [ui/table-header-cell]
                           [ui/table-header-cell {:style {:cursor :pointer
                                                          :color (when (= @organize-by* "timestamp") "#2185d0")}
                                                  :on-click (fn [ev]
                                                              (reset! organize-by* "timestamp")
                                                              (println @organize-by*))}
                            [ui/icon {:name "sort"}]
                            "Timestamp"]
                           [ui/table-header-cell {:style {:cursor :pointer
                                                          :color (when (= @organize-by* "importance") "#2185d0")}
                                                  :on-click (fn [ev]
                                                              (println "clicked")
                                                              (reset! organize-by* "importance")
                                                              (println @organize-by*))}
                            [ui/icon {:name "sort"}]
                            "Importance"]]]
            table-body [ui/table-body
                        (map-indexed
                          (fn [i element]
                            (results-module i element))
                          (reverse
                            (sort-by
                              (if (= @organize-by* "timestamp") :timestamp :importance)
                              @all-surveys*)))]]
        [:div.speaker-page
         [:div.content-section
          [create-module]
          [ui/table {:size "large"
                     :basic true
                     :attached :top
                     :style {:border-radius "0px"}}
           table-header
           table-body]]]))))
