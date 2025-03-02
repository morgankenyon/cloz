(ns cloz.core-test
  (:require [clojure.test :refer :all]
            [cloz.core :refer :all]))

(def valid-wasm-header [0 97 115 109 1 0 0 0])
(def valid-wasm-program [0, 97, 115, 109, 1, 0, 0, 0, 1, 5, 1, 96, 0, 1, 127, 3, 2, 1, 0, 7, 8, 1, 4, 109, 97, 105, 110, 0, 0, 10, 6, 1, 4, 0, 65, 23, 11])
(def parsed-program {:01_type {:sec_name "01_type", :content (list 1 5 1 96 0 1 127)}, :03_func {:sec_name "03_func", :content (list 3 2 1 0)}, :07_export {:sec_name "07_export", :content (list 7 8 1 4 109 97 105 110 0 0)}, :10_code {:sec_name "10_code", :content (list {:num-of-params 0, :body (list 65 23 11)})}})
(def simple-code-body (list 65 23 11))

(defn get-program [file-name]
  valid-wasm-program)
(deftest can-validate-good-header
  (testing "can ensure header is valid"
    (is (= true (is-wasm-header-valid? valid-wasm-header)))))

(deftest can-validate-good-header-and-extra
  (testing "can ensure header is valid"
    (is (= true (is-wasm-header-valid? (conj valid-wasm-header [1 1 1 2]))))))

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

(deftest can-parse-basic-program
  (testing "can parse program that returns a number"
    (let [parsed-program (load-and-validate-wasm get-program "main.wasm")]
      ;;(println parsed-program)
      (is (= 4 (count parsed-program)))
      (is (contains? parsed-program :01_type))
      (is (not (contains? (parsed-program :01_type) :rest)))
      (is (contains? parsed-program :03_func))
      (is (not (contains? (parsed-program :03_type) :rest)))
      (is (contains? parsed-program :07_export))
      (is (not (contains? (parsed-program :07_type) :rest)))
      (is (contains? parsed-program :10_code)))))

(deftest can-run-simple-code-body
  (testing "can run a simple code body that only returns a number"
    (let [output (run-code [] simple-code-body)]
      ;;(println output)
      (is (= 23 (first output))))))