(ns cloz.core
  (:require [clojure.java.io :as io])
  (:import [java.io ByteArrayOutputStream]))


(defn file-to-bytes
  "Reads bytes from file"
  [file-path]
  (with-open [xin (io/input-stream file-path)
              xout (ByteArrayOutputStream.)]
    (io/copy xin xout)
    (.toByteArray xout)))

(defn get-file-path
  "Determines appropriate file path from resource file"
  [file-name]
  (.getFile (clojure.java.io/resource file-name)))

(defn get-bytes
  [file-name]
  (file-to-bytes (get-file-path file-name)))

(defn is-wasm-header-valid?
  "Validates that wasm header is correct"
  [bytes]
  (= (take 8 bytes) [0 97 115 109 1 0 0 0]))

(defn calculate-length
  [wasm-bytes]
  (first (drop 1 wasm-bytes)))

(defn function-to-map
  [wasm-bytes]
  ;(println wasm-bytes)
  {:num-of-params (first wasm-bytes) :body (drop 1 wasm-bytes)})

(defn extract-functions
  [wasm-bytes num-of-funcs]
  ;(println wasm-bytes)
  (let [func-length (first wasm-bytes)
        func-bytes (take func-length (drop 1 wasm-bytes))]
    (if (= 1 num-of-funcs)
      (list (function-to-map func-bytes))
      (concat (list (function-to-map func-bytes)) (extract-functions (drop (+ 1 func-length) wasm-bytes) (- num-of-funcs 1))))))

(defn get-rest
  [wasm-bytes length]
  (drop (+ length 2) wasm-bytes))
 
(defn build-type-section
  [wasm-bytes length]
  ;(println "in build-type-section")
  {:sec_name "01_type" :content (take (+ 2 length) wasm-bytes) :rest (get-rest wasm-bytes length)})

(defn build-function-section
  [wasm-bytes length]
  {:sec_name "03_func" :content (take (+ 2 length) wasm-bytes) :rest (get-rest wasm-bytes length)})

(defn build-export-section
  [wasm-bytes length]
  {:sec_name "07_export" :content (take (+ 2 length) wasm-bytes) :rest (get-rest wasm-bytes length)})

(defn build-code-section
  [wasm-bytes]
  (let [num-of-funcs (first (drop 2 wasm-bytes))]
    ;(println wasm-bytes)
    {:sec_name "10_code" :content (extract-functions (drop 3 wasm-bytes) num-of-funcs)}))

(defn extract-section
  [wasm-bytes]
  (let [section-type (first wasm-bytes)
        section-length (calculate-length wasm-bytes)]
    (cond
      ;; type section
      (= section-type 1) (build-type-section wasm-bytes section-length)
      ;; function section
      (= section-type 3) (build-function-section wasm-bytes section-length)
      ;; export section
      (= section-type 7) (build-export-section wasm-bytes section-length)
      ;; code section, don't need length since it's the last section
      (= section-type 10) (build-code-section wasm-bytes)
                                              :else {})))

(defn recursive-map-builder
  [f input iterations]
  (loop [current-input input
         results {}
         count 0]
    (if (= count iterations)
      results
      (let [result (f current-input)
            next-input (:rest result)
            result-key (keyword (:sec_name result))]
        (recur next-input
               (assoc results result-key (dissoc result :rest))
               (inc count))))))

(defn recursively-extract-sections
  [wasm-bytes]
  (recursive-map-builder extract-section wasm-bytes 4))

(defn pop-and-return [stack]
  [(peek stack) (pop stack)])

(defn binary-op [op stack wasm-bytes]
  (let [add-result (op (first stack) (second stack))
        new-stack (conj (vec (drop 2 stack)) add-result)
        current-bytes (drop 1 wasm-bytes)]
    [new-stack current-bytes]))
(defn run-code 
  [stack wasm-bytes]
  ;; (println stack)
  ;; (println wasm-bytes)
  ;; (println "")
  (if (empty? wasm-bytes)
    (pop-and-return stack)
    (let [command (first wasm-bytes)]
      (cond
        (= 11 command) (pop-and-return stack)
        ;; add int to stack
        (= 65 command)
        (let [new-stack (conj stack (first (drop 1 wasm-bytes)))
              current-bytes (drop 2 wasm-bytes)]
          (run-code new-stack current-bytes))
        ;; i32.addition
        (= 106 command)
        (let [op-result (binary-op + stack wasm-bytes)]
          (run-code (first op-result) (second op-result)))
        (= 107 command)
        (let [op-result (binary-op - stack wasm-bytes)]
          (run-code (first op-result) (second op-result)))
        :else (throw (Exception. (str "'" command "' is an unsupported operator type")))))))

(defn load-and-validate-wasm
  [get-wasm-bytes file-name]
  (let [bytes (get-wasm-bytes file-name)]
    (if (is-wasm-header-valid? bytes)
      (recursively-extract-sections (drop 8 bytes))
      (throw (Exception. "Need valid wasm header")))))

(defn run-wasm-file
  [get-wasm-bytes file-name]
  (let [parsed-file (load-and-validate-wasm get-wasm-bytes file-name)]
    (first (run-code [] (:body (first (:content (:10_code parsed-file))))))))

(defn -main [& args]
  (let [output (run-wasm-file get-bytes "subtraction.wasm")]
    (println output)))
