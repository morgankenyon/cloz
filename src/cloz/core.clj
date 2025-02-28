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
  [bytes expected-start]
  (= (take (count expected-start) bytes) expected-start))

(defn calculate-length
  [wasm-bytes]
  (first (drop 1 wasm-bytes)))

(defn build-type-section
  [wasm-bytes length]
  "in build-type-section")
(defn extract-section
  [wasm-bytes]
  (let [section-type (first wasm-bytes)
        section-length (calculate-length wasm-bytes)]
    (cond
      ;; type section
      (= section-type 1) (build-type-section wasm-bytes section-length)
      ;; function section, update to function
      (= section-type 3) (build-type-section wasm-bytes section-length)
      ;; export section, update to export
      (= section-type 7) (build-type-section wasm-bytes section-length)
      ;; code section, update to code
      (= section-type 10) (build-type-section wasm-bytes section-length)
      :else (list "unknown" section-length))))

(defn load-and-validate-wasm
  [file-name]
  (let [bytes (get-bytes file-name)]
    (if (is-wasm-header-valid? bytes [0 97 115 109 1 0 0 0])
      (extract-section (drop 8 bytes))
      "Invalid")))
(defn -main [& args]
  (println (load-and-validate-wasm "main.wasm")))
