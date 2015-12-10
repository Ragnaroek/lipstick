(ns lipstick.sticks.total-file-mapping-test
  (:require [clojure.test :refer :all]
            [lipstick.sticks.total-file-mapping :refer :all]))

(deftest should-extract-constructor-javadocs
  (is (= (transform-to-source-file "/a/b/c" ".html" "/1/2/3" ".java" "/1/2/3/x/y/Test.java")
         "/a/b/c/x/y/Test.html")))

(deftest should-not-match-exclude
  (is (= (not-matches-one [".*/shared/.*"] "/foo/bar/baz"))))

(deftest should-match-second-exclude
  (is (= (not-matches-one ["nothing", ".*/shared/.*"] "/foo/shared/baz"))))
