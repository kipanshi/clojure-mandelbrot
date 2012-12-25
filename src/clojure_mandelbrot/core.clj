(ns clojure-mandelbrot.core
  (:gen-class)
  (:use clojure.java.io))

;; CONSTANTS
(def image-height 1000)
(def image-width 1000)
(def color-depth 65355)

(def scale-factor 4)
(def x-offset 1)
(def y-offset 2)

(def min-re (/ (+ -2.0 x-offset) scale-factor))
(def max-re (/ (+ 1.0 x-offset) scale-factor))
(def min-im (/ (+ -1.2 y-offset) scale-factor))
(def max-im (+ min-im (* (- max-re min-re) (/ image-height image-width))))

(def re-factor (/ (- max-re min-re) (- image-width 1)))
(def im-factor (/ (- max-im min-im) (- image-height 1)))

(def max-iterations 70)

;; Foreground and background pixels are those that belong and not belong
;; to the Mandelbrot set respectively
(def foreground-pixel '(0 0 0))
(def background-red-value 5000)
(def background-green-value 3000)
(def background-blue-value 0)

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

(defn background-pixel
  "Calculate the color of background pixel depending on number of iterations"
  [n]
  (if (= n max-iterations)
    (list 0 0 0)
    (list background-red-value background-green-value
          (+ background-blue-value (int (* color-depth (/ n max-iterations))))))
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
        (background-pixel n))
      foreground-pixel))
)

(defn process-pixel
  "Process a pixel"
  [wrtr x y c-im]
  (let [c-re (+ min-re (* x re-factor))
        z-re c-re
        z-im c-im]
    (write-pixel wrtr (process-iteration 0 c-re c-im z-re z-im))
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
