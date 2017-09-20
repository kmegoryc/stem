(ns stem.pages.home
  (:require [think.semantic-ui :as ui]))

(defn home-page []
  [:div.home-page
   [ui/header {:size "large"} "Fuse: A Platform for Audience Members & Speakers"]
   [:div "If only there was a way to let them know what you’re thinking, without disrupting the whole event or drawing unwanted attention to yourself. Is the teacher going too fast? Too slow? Should they explain that in another way? Students would be able to collaboratively give the teacher their live feedback, so he/she can improve the way they deliver information during lecture. With Fuse, teachers can post topic modules for their students to provide feedback on. The teacher's dashboard shows the average results of students' feedback."]
   [ui/divider]])
