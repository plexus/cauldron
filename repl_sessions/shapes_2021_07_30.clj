(ns shapes-2021-07-30
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as c]))

(def me (wc/player "sunnyplexus"))

;; a place in the world where I'm going to build stuff
(def loc [2259 75 105])

(defn move-to
  "Take a collection of locations and shift them all to a position relative to
  `loc`. Note that [[wc/add]] is polymorphic, it can take locations as
  a :x/:y/:z map, or as a [x y z] vector, or a Glowstone Location object. It
  returns a value of the same type as its first argument."
  [coll xyz]
  (map #(wc/add (wc/add % loc) xyz) coll))

;; Orb
(->
 ;; Generate the shape with a list comprehension At this point we don't care yet
 ;; about actual world coordinates, we just generate the data as if the
 ;; structure is set at the center of the world, so centered around [0 0 0]
 (let [size 15]
   (for [x (range (- size) size)
         y (range (- size) size)
         z (range (- size) size)
         :when (< (wc/distance [0 0 0] [x y z]) size)]
     {:x x
      :y y
      :z z
      ;; Mix it up a bit, we'll make one block in five mossy
      :material (if (< (rand-int 100) 20)
                  :mossy-cobblestone
                  :cobblestone)}))
 ;; Move it to the location we picked, and also 10 blocks up, otherwise it'll be
 ;; half-submerged
 (move-to [0 10 0])
 ;; Now we have a collection of maps, each describing a single block
 ;; with :x/:y/:z, time to pass this to `set-blocks` so the world actually
 ;; updates.
 wc/set-blocks)

;; By doing it in a single step we can now conveniently undo it and try again if
;; we're not yet happy with the result
(wc/undo!)

;; Cylinder
(-> (let [size 12]
      (for [x (range (- size) size)
            y (range (- size) size)
            z (range (- size) size)
            ;; Almost the same as before, but instead of taking the distance to the
            ;; center of the circle (`[0 0 0]`), we take the distance to `[0 y 0]`,
            ;; i.e. the center of the cylinder, at the height of the current block
            ;; Alternative: (wc/distance [0 0 0] [x 0 z])
            ;;
            ;; Subtracting 0.5 from the size seems to make the shape feel a bit
            ;; more rounded, YMMV
            :when (< (wc/distance [0 y 0] [x y z]) (- size 0.5))]
        {:x x
         :y y
         :z z
         ;; Make this one a bit more colorful
         :material :blue-glazed-terracotta}))
    ;; Moving it a bit to the side so it doesn't collide with the orb
    (move-to [30 10 0])
    wc/set-blocks)

;; Pyramid
(-> (let [size 6]
      (for [;; Iterate over `y` (the height) as before, but do this first so we can
            ;; use the value of `y` for determining `x` and `z`
            y (range (- size) size)
            ;; As `y` goes up, loop over a smaller range of `x` and `z`
            x (range (- (- size y)) (- size y))
            z (range (- (- size y)) (- size y))]
        {:x x
         :y y
         :z z
         :material :green-glazed-terracotta}))
    (move-to [0 15 30])
    wc/set-blocks)

;; Prism / Roof
;; By only changing one of the dimensions instead of two we get a "roof" shape
(-> (let [size 6]
      (for [y (range (- size) size)
            x (range (- (- size y)) (- size y))
            z (range (- size) size)]
        {:x x
         :y y
         :z z
         :material :grass}))
    (move-to [0 15 -30])
    wc/set-blocks)

;; It's quite common in Minecraft to use staircases to make more gradual roofs
;; But this example illustrates that the naive approach has two problems: we
;; need to make sure the staircases are rotated correctly, and we only want
;; the "top" blocks be staircases, not all inner ones.
;;
;; Dealing with rotation requires setting a 4-bit "material data" value, I tend
;; to just experimentally determine which one I need by trying numbers starting
;; with 0,1 etc. Usually the lower bits are the main ones that matter.
;;
;; Note that this is eventually going to change, newer versions of Minecraft no
;; longer use this 4-bit material data, instead they have created separate block
;; types for all variants in a process known as "the flattening". Glowstone is
;; working on implementing that, so this code will likely no longer work in the
;; future
(-> (let [size 6]
      (for [y (range (- size) size)
            x (range (- (- size y)) (- size y))
            z (range (- size) size)]
        (let [top? (#{(- (- size y)) (dec (- size y))} x)]
          {:x x
           :y y
           :z z
           :material (if top?
                       :wood-stairs
                       :brick)
           :data (cond (not top?) 0
                       (< x 0) 0
                       :else 1)})))
    (move-to [30 15 -30])
    wc/set-blocks)

;; An alternative is to use the cursor API, this has the benefit that it has
;; rotation logic built-in, so blocks will be aligned with the direction the
;; cursor is going in.

(def init-cursor
  (->
   ;; Initial prep setting up the cursor, choose the starting location and
   ;; direction, and set the material we're "drawing" with
   (c/start (wc/add loc [10 15 -50]) :south)
   (c/material :wood-stairs)))

;; This "cursor" is really just a map which looks like this (plus a few other
;; things that I've omitted). The cursor namespace has helpers for dealing with
;; all aspects of this data structure, but at the end of the day it's still
;; just a clojure map, and you can use regular Clojure functions if that makes
;; sense.
;; :blocks is currently an empty map, that's going to fill up as we walk
;; and "draw". When your structure is ready you pass the cursor to [[c/build!]]
;; to create it in the world.

init-cursor
;; =>
;; {:x 2269
;;  :y 90
;;  :z 55
;;  :dir :south
;;  :material :wood-stairs
;;  :rotate-block 0
;;  :face-direction? true
;;  :blocks #{}}

;; I'll build up the rest step by step so you can see how to build this up, the
;; main thing is that if we now simply move the cursor ten steps forward, we get
;; ten staircase blocks, and they all line up

(-> init-cursor
    (c/steps 10)
    c/build!)

;; Now we basically want to go back to our starting point, go one block up and
;; one block to the side, and repeat this. Our cursor is going south, so that
;; means going 1 west and 1 up, but instead of hard-coding that cardinal
;; direction, I'm going to use relative movements: turn 90 degrees (clockwise),
;; go 1 forward, 1 up, then rotate 90 degrees back, before doing the steps
;; again.

(-> init-cursor
    (c/rotate 2)
    (c/move 1 :forward 1 :up)
    (c/rotate -2)
    (c/steps 10)
    c/build!)

;; So far we've drawn one row at a time, but if we want to do multiple in one go
;; then we have the problem that after each row we need to return to the
;; beginning of the row, so we can go up-and-left and draw the next row. For
;; cases like this there's a helper function called `excursion`, which will
;; perform an operation (presumably making steps to draw blocks), then returns
;; the cursor to the original position.

(-> init-cursor
    (c/excursion #(c/steps % 10))

    (c/rotate 2)
    (c/move 1 :forward 1 :up)
    (c/rotate -2)
    (c/excursion #(c/steps % 10))

    (c/rotate 2)
    (c/move 1 :forward 1 :up)
    (c/rotate -2)
    (c/excursion #(c/steps % 10))
    c/build!)

;; Now we have a repeating pattern, and we can have that repeat with [[c/reps]]
;; `reps` is a higher order function which applies a cursor operation a given
;; number of times. We'll use it to draw 10 rows of roof tiles on each side.
;; It can be helpful to pull this inner "walks" into their own named functions.
(-> init-cursor
    (c/reps 10
            (fn [c]
              (-> c
                  (c/excursion #(c/steps % 10))
                  (c/rotate 2)
                  (c/move 1 :forward 1 :up)
                  (c/rotate -2))))
    (c/build!))

;; Now to draw the other side of the roof we jump over to the opposite diagonal, rotate 180 degrees, and do the same thing again.
(defn roof-row [c]
  (-> c
      (c/excursion #(c/steps % 10))
      (c/rotate 2)
      (c/move 1 :forward 1 :up)
      (c/rotate -2)))

(-> init-cursor
    ;; Use excursion for the first half of the roof, otherwise the cursor ends
    ;; up at the top and we need to bring it down again. This way we can do both
    ;; halves quite independently
    (c/excursion #(c/reps % 10 roof-row))
    ;; Go to the end of the row
    (c/move 9)
    ;; Turn 90 degrees
    (c/rotate 2)
    ;; Move to the corner where the second roof half will start
    (c/move 19)
    ;; Turn again, we're now facing the other way
    (c/rotate 2)
    ;; Draw the second half!
    (c/reps 10 roof-row)
    c/build!)
