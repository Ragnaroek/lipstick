(ns lipstick.core
  (:require [clojure.string :as string] [me.raynes.fs :as fs])
  (:use lipstick.ast.query))

;TODO Move comment stuff to separate module
;TODO argument parser

;TODO read interactive input file
;TODO write input file back to code

(defn +? [a b]
  (cond
    (not a) b
    (not b) a
    :else (+ a b)))

(defn count-distinct [docs]
  (reduce (fn [m doc] (update m doc (partial +? 1))) {} docs))

(defn to-str-without-newline [javadoc]
  (string/replace (. javadoc toString) #"\n" ""))

(defn extract-one-line-comment-and-hashcode [javadoc]
  (let [javadoc-string (. javadoc toString)]
    [(. javadoc-string hashCode) (to-str-without-newline javadoc-string)]))

(defn constructor-javadocs [file]
  (map extract-one-line-comment-and-hashcode (-> (compilation-unit file) type-declarations constructors javadoc)))

(defn convert-to-string [result]
  (string/join "\n" (map #(str (format "%10d" (first (first %))) "\tconstructor\t[]\t" (second %) "\t" (second (first %))) result)))

(defn save-to-file [str]
  ;TODO file as parameter
  (spit "/Users/mb/lipstick-constructors.txt" str))

(defn extract-comments []
  ;TODO input file as parameter
  (let [docs (count-distinct
                (apply concat (fs/walk (fn [r d fs]
                                        (mapcat (fn [f]
                                                  (when (. f endsWith ".java")
                                                    (constructor-javadocs (str r "/" f)))) fs)) "/Users/mb/projekte/hunter-deploy/src/main/java/")))]
      (save-to-file (convert-to-string (sort #(- (second %2) (second %)) (seq docs))))))

(defn -main [& args]
   (extract-comments))
