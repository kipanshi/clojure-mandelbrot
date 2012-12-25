(ns clojure-mandelbrot.core
  (:gen-class)
  (:use clojure.java.io))

;; CONSTANTS
(def image-height 1000)
(def image-width 1000)
(def color-depth 65355)

(def min-re -2.0)
(def max-re 1.0)
(def min-im -1.2)
(def max-im (+ min-im (* (- max-re min-re) (/ image-height image-width))))

(def re-factor (/ (- max-re min-re) (- image-width 1)))
(def im-factor (/ (- max-im min-im) (- image-height 1)))

(def max-iterations 70)

(def background-pixel '(0 0 32000))
(def foreground-pixel '(65355 65355 65355))

(def OUTPUT-FILE "Mandelbrot.ppm")

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

(defn process-iteration
  "Process an iteration for the pixel"
  [n c-re c-im z-re z-im]
  (let [z-re2 (* z-re z-re)
        z-im2 (* z-im z-im)]
    (if (< n max-iterations)
      (if (< (+ z-re2 z-im2) 4)
        (process-iteration (inc n) c-re c-im
              (+ c-re (- z-re2 z-im2)) (+ (* 2 z-re z-im) c-im))
        false)
      true))
)

(defn process-pixel
  "Process a pixel"
  [wrtr x y c-im]
  (let [c-re (+ min-re (* x re-factor))
        z-re c-re
        z-im c-im]
    (if (true? (process-iteration 0 c-re c-im z-re z-im))
      (write-pixel wrtr foreground-pixel) (write-pixel wrtr background-pixel))
))

(defn process-y
  "Process each row"
  [wrtr y]
  (let [c-im (- max-im (* y im-factor))]
    (dorun (map (fn [x] (process-pixel wrtr x y c-im)) (range 0 image-width))))
)

(defn -main
  "Calculate Mandelbrot set and write image file."
  [& args]
  (with-open [wrtr (writer OUTPUT-FILE)]
    (write-header wrtr image-width image-height color-depth)
    (dorun (map (fn [y] (process-y wrtr y)) (range 0 image-height)))
    )
)
