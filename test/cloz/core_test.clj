(ns cloz.core-test
  (:require [clojure.test :refer :all]
            [cloz.core :refer :all]))

(deftest can-validate-good-header
  (testing "can ensure header is valid"
    (is (= true (is-wasm-header-valid? [0 97 115 109 1 0 0 0])))))

(deftest can-validate-good-header-and-extra
  (testing "can ensure header is valid"
    (is (= true (is-wasm-header-valid? [0 97 115 109 1 0 0 0 1 1 1 2])))))

(deftest can-catch-bad-header
  (testing "can ensure validations fails for bad header"
    (is (= false (is-wasm-header-valid? [10 20])))))

(deftest can-calculate-length
  (testing "can-calculate-length"
    (is (= 4 (calculate-length [10 4 1 2 3 4])))))

(deftest can-extract-list-to-function
  (testing "can take a list and turn it into our map"
    (let [func-map (function-to-map [1 2 3 4 5])]
      (is (contains? func-map :num-of-params))
      (is (contains? func-map :body))
      (is (= (:num-of-params func-map) 1))
      (is (= (:body func-map) [2 3 4 5])))))

(deftest can-extract-one-function
  (testing "can extract one function from code section"
    (let [func-list (extract-functions [4 0 65 23 11] 1)]
      (is (= 1 (count func-list)))
      (is (= 2 (count (first func-list)))))))

(deftest can-extract-two-functions
  (testing "can extract two functions from code section"
    (let [func-list (extract-functions [4 0 65 23 11 4 0 65 24 11] 2)]
      (is (= 2 (count func-list)))
      (is (= (:body (first func-list)) [65 23 11]))
      (is (= (:body (second func-list)) [65 24 11])))))