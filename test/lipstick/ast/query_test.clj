(ns lipstick.ast.query-test
  (:require [clojure.test :refer :all]
            [lipstick.ast.query :refer :all]))


(deftest should-collect-types-in-simple-javadoc-file
  (let [type (first (type-declarations (compilation-unit "test/lipstick/testfiles/SimpleJavaDoc.java")))]
    (is (= (. (. type getName) getIdentifier) "SimpleJavaDoc"))))

; TODO Write some tests for constructors
; TODO Write some tests for Javadocs
