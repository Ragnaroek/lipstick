(ns lipstick.core
  (:require [clojure.string :as string] [me.raynes.fs :as fs])
  (:use lipstick.ast.query))

(defn +? [a b]
  (cond
    (not a) b
    (not b) a
    :else (+ a b)))

(defn count-distinct [docs]
  (reduce (fn [m doc] (update m doc (partial +? 1))) {} docs))

(defn constructor-javadocs [file]
  (map (fn [n] (string/replace (. n toString) #"\n" ""))
    (-> (compilation-unit file) type-declarations constructors javadoc )))

(defn foo []

  (println (count-distinct (flatten (fs/walk (fn [r d fs]
              (mapcat (fn [f] (when (. f endsWith ".java") (constructor-javadocs (str r "/" f)))) fs)) "/Users/mb/projekte/hunter-deploy/src/main/java"))))

)

(defn -main [& args]
   (foo))
