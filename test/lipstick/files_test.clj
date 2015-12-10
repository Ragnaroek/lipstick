(ns lipstick.files-test
  (:require [clojure.test :refer :all]
            [lipstick.files :refer :all]))

(defn- file-name [file]
  (. (new java.io.File file) getName))

(defn- file-names [file-list]
  (set (map file-name file-list)))

(deftest should-collect-all-java-files-in-test-folder
  (is (= (file-names (walk-javafiles-with list "test/lipstick/testfiles/filesTest"))
         #{"A1.java", "A21_1.java", "A21_2.java", "B_1.java", "B_2.java"})))

(deftest should-collect-all-text-files-in-test-folder
  (is (= (file-names (walk-files-with list "test/lipstick/testfiles/filesTest" ".txt"))
         #{"NotJava.txt"})))
