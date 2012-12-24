(ns clojure-mandelbrot.core-test
  (:use clojure.test
        clojure-mandelbrot.core
        clojure.java.io))

(def TEST-FILE "/tmp/clojure-mandelbrot.core-test.ppm")


(deftest test-write-to-test-file
  (testing "Writing to test file"
    (with-open [wrtr (writer TEST-FILE)]
      (.write wrtr "hello world"))
    (is (= "hello world" (slurp TEST-FILE)))
))

(deftest test-write-pixel
  (testing "Writing pixel to the opened writer"
    (with-open [wrtr (writer TEST-FILE)]
      (write-pixel wrtr '(0 65355 0)))
    (is (= "0 65355 0 " (slurp TEST-FILE)))
))

(deftest test-write-pixels
  (testing "Write sequence of pixels"
    (with-open [wrtr (writer TEST-FILE)]
      (write-pixels wrtr '((0 65355 0) (0 0 0))))
    (is (= "0 65355 0 0 0 0 " (slurp TEST-FILE)))
))

(deftest test-write-header
  (testing "Write PPM header to the opened writer"
    (with-open [wrtr (writer TEST-FILE)]
      (write-header wrtr 600 800 65355))
    (is (= "P3\n600 800\n65355\n" (slurp TEST-FILE)))
))

(deftest test-write-image
  (testing "Write PPM image to the given path"
    (write-image TEST-FILE 2 3 65355
      '((65355 0 0) (0 65355 0) (0 0 65355)
              (65355 65355 0) (0 65355 65355) (65355 0 65355)))
    (is (= "P3\n2 3\n65355\n65355 0 0 0 65355 0 0 0 65355 65355 65355 0 0 65355 65355 65355 0 65355 " (slurp TEST-FILE)))
))