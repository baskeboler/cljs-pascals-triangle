(ns pascal.core
  (:require [reagent.core :as r]
            [reagent.dom :refer [render]]
            [pascal.styles :refer [css-str]]
            [goog.string :refer [unescapeEntities]]
            [goog.dom :refer [getElement getElementByClass]]
            ["pagemap" :as pagemap]))
(defonce app-root (. js/document (getElementById "root")))
(def ms #js
  {"viewport"  (getElementByClass "triangle")
   "back"     "rgba(0,0,0,0.02)"
   "view"     "rgba(255,0,0,0.05)"
   "drag"     "rgba(0,0,0,0.10)"
   "styles"   #js {"header,footer,section,article,div.row" "rgba(0,0,0,0.08)"
                   "span.col"                              "rgba(0,0,0,0.3)"}
   "interval" nil})
(def pascal-row-aux
  (memoize
   (fn [n]
     (loop [row               1
            summed-partitions []]
      (cond
        (= n 0) '(1)
        (= n row)
        (concat '(1) summed-partitions '(1))
        :else   (recur (inc row)
                     (map (partial apply +)
                          (partition 2 1
                                     (concat '(1) summed-partitions '(1))))))))))
(defn pascal-row [n]
  (if (= n 0)
    '(1)
     (let [previous-row (pascal-row-aux (dec n))
           sums (map (partial apply +) (partition 2 1 previous-row))]
       (concat '(1) sums '(1)))))

(defn pascal-element [col row]
  (if  (<= col row)
   (get
    (vec
     (pascal-row row))
    col)
   0))
(defonce row-count (r/atom 5))

(defn inc-rows []
  (swap! row-count + 5))
  

(defn debug-panel-data [{:keys [col row] :as arg}]
  (when (and (> col 0) (> row 1) (<= col row))
    (let [n (pascal-element col row)
          [t1 t2] (mapv #(pascal-element % (dec row)) [ (dec col) col])]
      (merge arg {:term1 t1 :term2 t2 :sum n}))))

(defn debug-panel [active-n]
  (when-let [debug-data (debug-panel-data active-n)]
    [:div.debug-panel
     [:div.term1
      [:span.position (unescapeEntities
                       (str "(" (dec (:col debug-data)) ", " (dec (:row debug-data)) ") &rarr;"))]
      (:term1 debug-data)]
     [:div.term2
      [:span.position (unescapeEntities
                       (str "(" (:col debug-data) ", " (dec (:row debug-data)) ") &rarr;"))]
      (str "+ " (:term2 debug-data))]
     [:hr]
     [:div.sum
      [:span.position (unescapeEntities
                       (str "(" (:col debug-data) ", " (:row debug-data) ") &rarr;"))]
      (:sum debug-data)]]))

(defn app []
  (let [active-n (r/atom nil)]
    (fn []
      [:div.app
       [:h1 "pascal's triangle"]
       [debug-panel @active-n]
       [:button
        {:on-click inc-rows}
        "more rows"]
       [:div.triangle
        (doall
         (for [i (range @row-count)]
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
                          "highlighted lighter")}

                n])
             (pascal-row i))]))]
       [:canvas#minimap]
       [:style css-str]])))

(defn mount-components! []
  (render [app] app-root))

(defn init! []
  (mount-components!)
  (pagemap (getElement "minimap") ms))

(init!)
(println "starting")
