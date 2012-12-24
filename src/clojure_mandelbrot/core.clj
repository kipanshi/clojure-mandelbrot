(ns clojure-mandelbrot.core
  (:gen-class))


;; CONSTANTS
(def image-height 1000)
(def image-width 1000)

(def min-re -2.0)
(def max-re 1.0)
(def min-im -1.2)
(def max-im (+ min-im (* (- max-re min-re) (/ image-height image-width))))

(def max-iterations 30)


(defn -main
  "Calculate Mandelbrot set and write image file."
  [& args]
  (println "Hello, World!")

)
