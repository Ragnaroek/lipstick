(ns lipstick.core
  (:require [clojure.string :as string] [clojure.java.io :as io] [me.raynes.fs :as fs])
  (:use lipstick.ast.query))

;TODO Move comment stuff to separate module

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

(defn all-constructor-javadocs-of-file [file]
  (-> (compilation-unit file) type-declarations constructors javadoc))

(defn constructor-javadocs [file]
  (map extract-one-line-comment-and-hashcode (all-constructor-javadocs-of-file file)))

(defn convert-to-string [result]
  (string/join "\n" (map #(str (format "%10d" (first (first %))) "\tconstructor\t[]\t" (second %) "\t" (second (first %))) result)))

(defn walk-javafiles-with [f path]
  (apply concat (doall (fs/walk (fn [r d fs]
                                   (mapcat (fn [file]
                                              (when (. file endsWith ".java")
                                                  (f (str r "/" file)))) fs)) path))))

(defn extract-comments [path out-file]
  (let [docs (walk-javafiles-with constructor-javadocs path)
        distinct (count-distinct docs)]
    (spit out-file (convert-to-string (sort #(- (second %2) (second %)) (seq distinct))))))

(defn print-usage-and-exit []
  (println "Usage:")
  (println "\tlipstick javadoc interactive <path> <out-file> -- read all constructor javadocs in <path> and dump to file <out-file>")
  (println "\tlipstick javadoc modify <path> <interactive-input> -- modify constructor javadocs in <path> with <interactive-input> file as input")
  (System/exit -1))

(defn do-javadoc-interactive [[path out-file]]
  (when (or (not path) (not out-file)) (print-usage-and-exit))
  (extract-comments path out-file))

(defn selected? [str]
  (= str "[x]"))

(defn parse-interactive-input-line [line]
  (string/split line #"\t"))

(defn add-hash-to-delete [s line]
    (let [parsed (parse-interactive-input-line line)]
      (if (selected? (nth parsed 2))
        (conj s (Integer/parseInt (. (nth parsed 0) trim)))
        s)))

(defn hashes-to-delete [interactive-input]
  (with-open [rdr (io/reader interactive-input)]
    (reduce add-hash-to-delete #{} (line-seq rdr))))

; removes this javadoc from its parent
(defn remove-javadoc! [javadoc]
  (when (not (= nil (. javadoc getParent)))
    (. (. javadoc getParent) setJavadoc nil)))

(defn write-back-file! [cu file content]
  ;We have to use the original string content that was used to build the compilation-unit
  ;otherwise rewrite throws exceptions
  (let [doc (new org.eclipse.jface.text.Document content)]
    (. (. cu rewrite doc nil) apply doc)
    (spit file (. doc get))))

(defn remove-constructor-and-rewrite-file [to-delete file]
  (println "Removing from file:" file)
  (let [content (slurp file)
        cu (compilation-unit-from-string content)]
    (. cu recordModifications)
    (doseq [javadoc (-> cu type-declarations constructors javadoc)]
      (when (contains? to-delete (. (. javadoc toString) hashCode))
        (remove-javadoc! javadoc)
        (write-back-file! cu file content)))))

(defn do-javadoc-modify [[path interactive-input]]
  (let [to-delete (hashes-to-delete interactive-input)]
    (walk-javafiles-with (partial remove-constructor-and-rewrite-file to-delete) path)))

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
