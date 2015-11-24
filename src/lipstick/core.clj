(ns lipstick.core
  (:require [clojure.string :as string] [clojure.java.io :as io] [me.raynes.fs :as fs])
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

(defn extract-comments [path out-file]
  (let [docs (count-distinct
                (apply concat (fs/walk (fn [r d fs]
                                        (mapcat (fn [f]
                                                  (when (. f endsWith ".java")
                                                    (constructor-javadocs (str r "/" f)))) fs)) path)))]
      (spit out-file (convert-to-string (sort #(- (second %2) (second %)) (seq docs))))))

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
  (let [line-parts (string/split line #"\t")]
    ;TODO reduce assoc-if selected?
    (println (nth line-parts 0) (nth line-parts 2) (selected? (nth line-parts 2)))))

(defn do-javadoc-modify [[path interactive-input]]
  (with-open [rdr (io/reader interactive-input)]
    (doseq [line (line-seq rdr)]
      (parse-interactive-input-line line))))

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
