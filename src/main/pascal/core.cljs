(ns pascal.core
  (:require [reagent.core :as r]
            [reagent.dom :refer [render]]
            [pascal.styles :refer [css-str]]))


(defonce app-root (. js/document (getElementById "root")))

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

(defonce row-count (r/atom 5))
(defn inc-rows []
  (swap! row-count + 5))

(defn app []
  (let [active-n (r/atom nil)]
    (fn []
      [:div.app 
       [:h1 "pascal's triangle"]
       [:div.debug-panel
        (str @active-n)]
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
       [:style css-str]])))

(defn mount-components! []
  (render [app] app-root))

(defn init! []
  (mount-components!))

(init!)
(println "starting")
