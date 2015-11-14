(ns lipstick.core
  (:require [clojure.string :as string] [me.raynes.fs :as fs])
  (:use lipstick.ast.query))

;TODO Move comment stuff to separate module
;TODO argument parser

;TODO Calc hashcode for javadoc

(defn +? [a b]
  (cond
    (not a) b
    (not b) a
    :else (+ a b)))

(defn count-distinct [docs]
  (reduce (fn [m doc] (update m doc (partial +? 1))) {} docs))

(defn to-str-without-newline [javadocs]
  (map (fn [javadoc]
          (string/replace (. javadoc toString) #"\n" "")) javadocs))

(defn constructor-javadocs [file]
  (to-str-without-newline (-> (compilation-unit file) type-declarations constructors javadoc)))

(defn convert-to-string [result]
  (string/join "\n" (map #(str "[]\t" (second %) "\t" (first %)) result)))

;(defn save-to-file [result]
;      (spit (convert-to-string result))))

(defn foo []
  (let [docs (count-distinct
                (flatten
                  (fs/walk (fn [r d fs]
                              (mapcat (fn [f]
                                        (when (. f endsWith ".java")
                                            (constructor-javadocs (str r "/" f)))) fs)) "/Users/mb/projekte/hunter-deploy/src/main/java/com/freiheit/commons/collections")))]

      (println (convert-to-string (sort #(- (second %2) (second %)) (seq docs))))))


(defn -main [& args]
   (foo))
