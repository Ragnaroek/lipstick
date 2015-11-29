(ns lipstick.util
  "A collection of misc util functions"
  (:require [clojure.string :as string]))


(defn +? [a b]
  "Add two numbers, computes nil if at least one of them is nil"
    (cond
      (not a) b
      (not b) a
      :else (+ a b)))

(defn count-distinct [values]
  "Calculates a histogram of the supplied value list"
  (reduce (fn [m val] (update m val (partial +? 1))) {} values))

(defn to-str-without-newline [o]
  "Coerces the supplied value to a string and remvoes all newlines"
  (string/replace (. o toString) #"\n" ""))
