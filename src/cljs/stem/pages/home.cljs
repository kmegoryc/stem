(ns stem.pages.home
  (:require [think.semantic-ui :as ui]))

(defn home-page []
  [:div.home-page
   [ui/header {:size "large"} "Fuse: A Platform for Audience Members & Speakers"]
   [:div "With this application, you can submit open feedback and questions to a speaker during their lecture. Additionally, the spaker can submit modules for their audience members to provide feedback on."]
   [ui/divider]])
