(ns lipstick.files
  (:require [me.raynes.fs :as fs]))

(defn walk-files-with [f path ending]
  (apply concat (doall (fs/walk (fn [r d fs]
                                  (mapcat (fn [file]
                                            (when (. file endsWith ending)
                                              (f (str r "/" file)))) fs)) path))))

(defn walk-javafiles-with [f path]
  (walk-files-with f path ".java"))
