(ns lipstick.util-test
  (:require [clojure.test :refer :all]
            [lipstick.util :refer :all]))

;; +?
(deftest should-be-nil-if-both-operands-are nil
  (is (= (+? nil nil)) nil))

(deftest should-be-first-value-if-second-is-nil
  (is (= (+? 42 nil) 42)))

(deftest should-be-second-value-if-first-is-nil
  (is (= (+? nil 32)) 32))

(deftest should-add-value-if-both-are-non-nil
  (is (= (+? 5 7) 12)))

;;count-distinct
(deftest should-be-empty-map-if-list-is-empty
  (is (= (count-distinct []) {})))

(deftest should-count-ones-if-all-distinct
  (is (= (count-distinct ["a" "b" "c" "d"]) {"a" 1, "b" 1, "c" 1, "d" 1})))

(deftest should-count-same
  (is (= (count-distinct ["a" "b" "a" "b" "a" "c" "d" "a" "d"]) {"a" 4, "b" 2, "c" 1, "d" 2})))

;;to-str-without-newline
(deftest should-be-same-value-if-no-newline
  (is (= (to-str-without-newline "test-without-newline") "test-without-newline")))

(deftest should-remove-newlines
  (is (= (to-str-without-newline "a\nb\n\nc\n\n\n") "abc")))
