(ns lipstick.sticks.javadoc
  "Interactive javadoc removal"
  (:require [clojure.string :as string])
  (:use lipstick.ast.query lipstick.ast.helper lipstick.util lipstick.files))

;; write histogram

(defn- extract-one-line-comment-and-hashcode [file-content javadoc]
  (let [javadoc-string (node-source file-content javadoc)]
    [(. javadoc-string hashCode) (to-str-without-newline javadoc-string)]))

(defn- all-constructor-javadocs-of-file [file]
  (-> (compilation-unit file) type-declarations constructors javadoc))

(defn- constructor-javadocs [file]
  (let [file-content (slurp file)]
    (map (partial extract-one-line-comment-and-hashcode file-content) (all-constructor-javadocs-of-file file))))

(defn constructor-javadoc-histogram [path]
  (count-distinct (walk-javafiles-with constructor-javadocs path)))

(defn- convert-to-string [result]
    (string/join "\n" (map #(str (format "%10d" (first (first %))) "\tconstructor\t[]\t" (second %) "\t" (second (first %))) result)))

(defn write-histogram [histogram out-file]
  "Write the histogram to out-file for interactive input"
  (spit out-file (convert-to-string (sort #(- (second %2) (second %)) (seq histogram)))))
