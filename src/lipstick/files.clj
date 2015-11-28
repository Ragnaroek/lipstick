(ns lipstick.files
  (:require [me.raynes.fs :as fs]))

(defn walk-javafiles-with [f path]
  (apply concat (doall (fs/walk (fn [r d fs]
                                  (mapcat (fn [file]
                                            (when (. file endsWith ".java")
                                              (f (str r "/" file)))) fs)) path))))
