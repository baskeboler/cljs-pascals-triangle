(ns build
  (:require  [clojure.java.shell :refer [sh]]
             [shadow.cljs.devtools.api :as shadow]))
(defn sh! [command]
  (println command)
  (println (sh "bash" "-c" command)))

(defn watch []
  (shadow/watch :app))

(defn ^:export setup-target-dir
  {:shadow.build/stage :compile-prepare}
  [build-state & args]
  (sh! "rm -rf public")
  (sh! "cp -rf resources/public public")
  build-state)

