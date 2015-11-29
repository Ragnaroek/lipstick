(ns lipstick.sticks.javadoc-test
  (:require [clojure.test :refer :all]
            [lipstick.sticks.javadoc :refer :all]))

(deftest should-extract-constructor-javadocs
  (is (= (constructor-javadoc-histogram "test/lipstick/testfiles/javadoc/")
         {[1897962308 "/**Konstruktor**/"] 2, [144245550 "/**Inner Konstruktor**/"] 2,
          [-1292472376 "/**Konstruktor2**/"] 1, [-1345266086 "/**Konstruktor   **/"] 1,
          [2085772278 "/**B2 Konstruktor**/"] 1})))

(deftest should-write-javadoc-histogram-to-file-ready-for-read-by-modify
  (write-histogram {[666555666 "original source 1"] 666, [278937289372 "original source 2"] 2} "test/lipstick/testfiles/tmpout/histogram.txt")
  (is (= (slurp "test/lipstick/testfiles/tmpout/histogram.txt")
         (str " 666555666\tconstructor\t[]\t666\toriginal source 1\n"
              "278937289372\tconstructor\t[]\t2\toriginal source 2"))))
