(ns lipstick.core
  (:require [clojure.string :as string])
  (:use lipstick.ast.query lipstick.files lipstick.util lipstick.sticks.javadoc))

(defn print-usage-and-exit []
  (println "Usage:")
  (println "\tlipstick javadoc interactive <path> <out-file> -- read all constructor javadocs in <path> and dump to file <out-file>")
  (println "\tlipstick javadoc modify <path> <interactive-input> -- modify constructor javadocs in <path> with <interactive-input> file as input")
  (System/exit -1))

(defn do-javadoc-interactive [[path out-file]]
  (when (or (not path) (not out-file)) (print-usage-and-exit))
  (extract-interative-input path out-file))

(defn do-javadoc-modify [[path interactive-input]]
  (when (or (not path) (not interactive-input)) (print-usage-and-exit))
  (remove-javadoc path interactive-input))

(defn do-javadoc [[sub-command & args]]
  (cond
    (= sub-command "interactive") (do-javadoc-interactive args)
    (= sub-command "modify") (do-javadoc-modify args)
    :else (print-usage-and-exit)))

(defn -main [& args]
  ;TODO use argument parser
  (let [[command & command-args] args]
    (cond
        (= command "javadoc") (do-javadoc command-args)
        :else (print-usage-and-exit))))

;lein run javadoc interactive "/Users/mb/projekte/hunter-deploy/src/main/java/com/freiheit/common" "/Users/mb/_lipstick.txt"
;lein run javadoc modify "/Users/mb/projekte/hunter-deploy/src/main/java/com/freiheit/common" "/Users/mb/_lipstick.txt"
