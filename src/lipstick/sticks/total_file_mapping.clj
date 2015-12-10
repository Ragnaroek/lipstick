(ns lipstick.sticks.total-file-mapping
  "Searches unreferenced files from source in target"
  (:require [clojure.string :as string] [clojure.set :as set])
  (:use lipstick.files))

(defn transform-to-source-file [source-folder source-ending target-folder target-ending target-file]
  (string/replace (string/replace target-file target-folder source-folder) target-ending source-ending))

(defn- print-git-rm [file]
  (println "git rm " file))

(defn- match? [file regex]
  (re-matches regex file))

(defn not-matches-one [excludes file]
  (not (some (partial match? file) (map re-pattern excludes))))

(defn total-file-mapping [source-folder source-ending target-folder target-ending excludes]
  (let [source-files (walk-files-with list source-folder source-ending)
        source-files-filtered (set (filter (partial not-matches-one excludes) source-files))
        target-files (walk-files-with list target-folder target-ending)
        mapped-targets (set (map (partial transform-to-source-file source-folder source-ending target-folder target-ending) target-files))]
    (doall (map print-git-rm (set/difference source-files-filtered mapped-targets)))))
