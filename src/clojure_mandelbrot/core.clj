(ns clojure-mandelbrot.core
  (:gen-class)
  (:use clojure.java.io))

;; CONSTANTS
(def image-height 1000)
(def image-width 1000)

(def min-re -2.0)
(def max-re 1.0)
(def min-im -1.2)
(def max-im (+ min-im (* (- max-re min-re) (/ image-height image-width))))

(def max-iterations 30)


;; FUNCTIONS
(defn write-pixel
  "Writes the pixel to the writer. Input is tuple of 3 integers representing RGB"
  [wrtr pixel-tuple]
  (.write wrtr (apply format (cons "%d %d %d " pixel-tuple)))
)

(defn write-pixels
  "Write a collection of pixels"
  [wrtr pixel-seq]
  (dorun (map (fn [pixel-tuple] (write-pixel wrtr pixel-tuple)) pixel-seq))
)

(defn write-header
  "Write PPN header"
  [wrtr width height color-depth]
  (.write wrtr (format "P3\n%d %d\n%d\n" width height color-depth))
)

(defn write-image
  "Writes image to the given path. Input is a collection of pixels"
  [write-path width height color-depth pixel-seq]
  (with-open [wrtr (writer write-path)]
    (write-header wrtr width height color-depth)
    (write-pixels wrtr pixel-seq)
    )
)

(defn -main
  "Calculate Mandelbrot set and write image file."
  [& args]
  (println "Hello, World!")

)
