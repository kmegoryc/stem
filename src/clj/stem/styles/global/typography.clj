(ns stem.styles.global.typography
  (:require [garden.units :refer [px percent]]
            [greenhouse.grid :refer [column span clearfix center stack align on cycle-props]]
            [garden.color :as color :refer [hsl rgb rgba]]))

(def colors
  {:green "#81C685"
   :dark-green "#73B439"
   :black "#373837"
   :pure-black "#000000"
   :pure-white "#ffffff"
   :text-dark "#161031"
   :light-black "#424544"
   :light-grey "#C0C1C1"
   :dark-grey "#2C2D2D"
   :old-mauve "#612F49"
   :blue-whale "#1A3045"
   :light-teal "#CCE7E0"
   :nav-1 "#000000"
   :nav-2 "#161031"
   :nav-3 "#254059"
   :nav-4 "#0E766A"
   :nav-5 "#5DB163"
   :nav-6 "#7CCAAE"
   :nav-7 "#CD4948"
   :nav-8 "#09564d"})

(def global
  [[:.-italic :em
    {:font-style :italic}]

   [:.-bold :strong
    {:font-style :bold}]

   [:.-white
    {:color (:pure-white colors)}]

   [:.-black
    {:color (:pure-black colors)}]

   [:.-underline
    {:box-shadow [[:inset 0 (px -3) 0 (:green colors)]]
     :color :inherit
     :background-color :transparent
     :transition [["backgroud-color 240ms cubic-bezier(0.2, 0.3, 0.25, 0.9)"]]}
    [:&:hover
     {:background-color (:green colors)
      :color :inherit}]]

   [:.message
    {:font {:family "Open Sans"
            :weight 200
            :size (px 20)}
     :color (:light-black colors)
     :letter-spacing (px 0.5)
     :margin-bottom 0}]

   [:p
    {:font {:family "Open Sans"
            :size (px 17)}
     :line-height (px 30)
     :color (:light-black colors)
     :margin-bottom (px 30)}]

   [:h1
    {:font-size (px 20)
     :font-weight 500
     :letter-spacing (px 2)
     :font-family "Roboto"
     :text-transform :uppercase
     :margin [[(px 70) (px 0) (px 20) 0]]}]

   [:a
    {:font-size (px 15)
     :line-height (px 13)
     :cursor :pointer
     :color :inherit
     :letter-spacing (px 1)
     :white-space :nowrap}]

   [:button
    {:font-size (px 15)}]

   [:li
    {:font-family "Open Sans"
     :font-size (px 17)}]

   [:.-hero
    [:*
     {:color (:pure-white colors)}]]])

(def text
  [:.-roboto
   {:font-family "Roboto"
    :font-weight 600
    :letter-spacing (px 1)
    :font-style :italic
    :font-size (px 15)}
   [:&.-light
    {:font-weight 300}]
   [:&.-bold
    {:font-weight 700}]
   [:&.-black
    {:font-weight 900}]
   [:&.-spacing
    {:letter-spacing (px 3)}]])

(def title
  [:.title
   {:font-family "Roboto"
    :font-size (px 30)
    :font-weight 500
    :letter-spacing (px 5)
    :margin-bottom (px 50)}
   (on :laptop [:&
                {:font-size (px 40)}])

   [:&.-ghost
    {:font-size (px 100)
     :font-weight 900
     :line-height 1}
    (on :laptop [:&
                 {:font-size (px 140)}])]

   [:&.-sub
    {:font-size (px 16)
     :position :relative
     :line-height (px 24)
     :letter-spacing (px 1)
     :margin-bottom (px 30)}
    [:&:after
     {:content "' '"
      :position :absolute
      :top (px -40)
      :left 0
      :width (px 100)
      :height (px 3)
      :background-color (:green colors)}]
    (on :laptop
        [:& {:font-size (px 18)
             :letter-spacing (px 1.5)}])
    (on :tablet
        [:& {:letter-spacing (px 1.5)}])]])

(def blog-typography
  [[:.blog-title
    {:font-size (px 28)
     :line-height (px 32)
     :font-weight 500
     :color (:nav-3 colors)
     :text-transform :uppercase
     :letter-spacing (px 1)}
    [:&:hover
     {:color (:nav-2 colors)}]]
   [:.blog-sub-header
    {:color (:light-black colors)
     :text-transform :uppercase
     :font-size (px 13)
     :letter-spacing (px 1)}]])

(def styles
  [global
   text
   title
   blog-typography])
