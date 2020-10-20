(ns pascal.styles
  (:require [garden.core :as gc :refer [css]]
            [garden.selectors :as gs]
            [garden.color :as color]))

(gs/defclass triangle)
(gs/defclass row)
(gs/defclass col)

(gs/defclass debug-panel)
(gs/defclass term2)
(gs/defclass term1)
(gs/defclass sum)
(gs/defclass position)

(gs/defclass highlighted)
(gs/defclass lighter)
(gs/defid minimap)


(def col-styles
  [[triangle {:width :fit-content}]
   [(gs/selector col)
    {:margin      :none
     :width :max-content
     :height :max-content
     :border      "1px solid lightgray"
     :padding     "0.2rem 0.6rem"
     :font-family "Playfair Display SC, serif"}]
   
   [(gs/selector row)
    {:display         :flex
     :justify-content :center}]
   [(gs/selector debug-panel)
    {:display          :block
     :position         :fixed
     :background-color :white
     :top              "1em"
     :padding          "1em"
     :right            "1em"
     :font-weight      :bold
     :font-size        "1.3em"
     :border-radius    "0.5em"
     :border           "1px solid lightgray"
     :opacity          0.7
     :text-align       :right}]
     
   [(gs/selector highlighted)
    {:font-weight :bold
     :color       :red}]
   [(gs/& highlighted lighter)
    {:color "#ff6666"}]
   [(gs/descendant debug-panel position)
    {:position :absolute
     :right    "100%"
     :color    :red
     :width    :max-content}]
   [minimap
    {:position :fixed
     :bottom   "1rem"
     :right    "1rem"
     ;; :height   "250px"
     :width    "300px"
     :z-index  100}]])

(def ^:export css-str (css {:pretty-print? true} col-styles))

