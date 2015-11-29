(ns lipstick.ast.query-test
  (:require [clojure.test :refer :all]
            [lipstick.ast.query :refer :all]))

(deftest should-collect-types-in-simple-file
  (let [type (first (type-declarations (compilation-unit "test/lipstick/testfiles/Simple.java")))]
    (is (= (. (. type getName) getIdentifier) "Simple"))))

(deftest should-collect-types-in-nested-file
  (let [types (type-declarations (compilation-unit "test/lipstick/testfiles/NestedClass.java"))]
    (is (= (map (fn [type] (. (. type getName) getIdentifier)) types) ["NestedClass" "Nested"]))))

; TODO Write some tests for constructors
; TODO Write some tests for Javadocs
