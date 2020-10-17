(ns pascal
  (:require [reagent.core :as r]
            [reagent.dom :refer [render]]
            [garden.core :refer [css] :as gc]
            [garden.selectors :as gs]
            [goog.style :as style]
            [goog.html.SafeStyleSheet :as ssheet]))

(gs/defclass row)
(gs/defclass col)
(gs/defclass debug-panel)
(gs/defclass highlighted)

(def col-styles
  [[(gs/selector col)
    {:margin    "0.2rem 0.6rem"}]
  
   [(gs/selector row)
    {:display :flex
     :justify-content :center}]
   [(gs/selector debug-panel)
    {:display :block
     :position  :fixed
     :top "1em"
     :right "1em"
     :border-radius "0.5em"
     :opacity 0.7}]
   [(gs/selector highlighted)
    {:font-weight :bold
     :color :red}]])
(def css-str (css col-styles))

;; (def ss (goog.html.SafeStyleSheet/fromConstant))
         ;; (goog.string.Const/from
         ;;  css-str)))


;; (goog.style/installSafeStyleSheet ss nil)

(defonce app-root (. js/document (getElementById "root")))

(defn pascal-row [n]
  (loop [row 1
         summed-partitions []]
    (cond
      (= n 0) '(1)
      (= n row)
      (concat '(1) summed-partitions '(1))
      :else (recur (inc row)
             (map (partial apply +)
                  (partition 2 1
                             (concat '(1) summed-partitions '(1))))))))

(defn app []
  (let [active-n (r/atom nil)]
    (fn []
      [:div.app 
       [:h1 "pascal's triangle"]
       [:div.debug-panel (str @active-n)]
       [:div.triangle
        (doall
         (for [i (range 20)]
           ^{:key (str "row-" i)}
           [:div.row
            ((comp doall map-indexed)
             (fn [j n]
               ^{:key (str "row-" i "-col-" j)}
               [:span.col
                {:on-mouse-over (fn [] (reset! active-n {:row i :col j}))
                 :class (cond
                          (and (= i (:row @active-n)) (= j (:col @active-n)))
                          "highlighted"
                          (and (= i (dec (:row @active-n)))
                               (#{j (inc j)} (:col @active-n)))
                          "highlighted")}

                n])
             (pascal-row i))]))]
       [:style css-str]])))

(defn mount-components! []
  (render [app] app-root))

(defn init! []
  (mount-components!))

(init!)
(println "starting")
