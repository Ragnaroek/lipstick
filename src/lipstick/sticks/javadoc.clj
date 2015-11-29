(ns lipstick.sticks.javadoc
  "Interactive javadoc removal"
  (:require [clojure.string :as string] [clojure.java.io :as io])
  (:use lipstick.ast.query lipstick.ast.helper lipstick.util lipstick.files))

;; common for write and modify

(defn- javadoc-hash [file-content javadoc]
  "Calculates a hash code that is used between the interactive input and modify to identify a comment to remove"
  (. (node-source file-content javadoc) hashCode))

;; write histogram

(defn- extract-one-line-comment-and-hashcode [file-content javadoc]
  [(javadoc-hash file-content javadoc) (to-str-without-newline (node-source file-content javadoc))])

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

(defn extract-interative-input [path out-file]
  (write-histogram (constructor-javadoc-histogram path) out-file))

;; modify TODO write more tests!
(defn- selected? [str]
  (= str "[x]"))

(defn- parse-interactive-input-line [line]
  (string/split line #"\t"))

(defn- add-hash-to-delete [s line]
  (let [parsed (parse-interactive-input-line line)]
    (if (selected? (nth parsed 2))
      (conj s (Integer/parseInt (. (nth parsed 0) trim)))
      s)))

(defn- hashes-to-delete [interactive-input]
  (with-open [rdr (io/reader interactive-input)]
    (reduce add-hash-to-delete #{} (line-seq rdr))))

; removes this javadoc from its parent
(defn- remove-javadoc! [javadoc]
  (when (not (= nil (. javadoc getParent)))
    (. (. javadoc getParent) setJavadoc nil)))

(defn- write-back-file! [cu file content]
  ;We have to use the original string content that was used to build the compilation-unit
  ;otherwise rewrite throws exceptions
  (let [doc (new org.eclipse.jface.text.Document content)]
    (. (. cu rewrite doc nil) apply doc)
    (spit file (. doc get))))

(defn- remove-constructor-and-rewrite-file [to-delete file]
  (println "Removing from file:" file)
  (let [content (slurp file)
        cu (compilation-unit-from-string content)]
    (. cu recordModifications)
    (doseq [javadoc (-> cu type-declarations constructors javadoc)]
      (when (contains? to-delete (javadoc-hash content javadoc))
        (remove-javadoc! javadoc)
        (write-back-file! cu file content)))))

(defn remove-javadoc [path interactive-input]
  (let [to-delete (hashes-to-delete interactive-input)]
    (walk-javafiles-with (partial remove-constructor-and-rewrite-file to-delete) path)))
