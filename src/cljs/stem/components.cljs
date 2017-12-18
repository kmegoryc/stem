(ns stem.components
  (:require [reagent.core :as reagent :refer [atom]]
            [think.semantic-ui :as ui]))

(defn nav [access]
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
      [:a {:href "/speaker"} access]]
     [ui/menu-item {:position :right}
      [:a {:href "/"} "SETTINGS"]]]]])
