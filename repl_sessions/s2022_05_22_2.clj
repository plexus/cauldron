(ns repl-sessions.s2022-05-22-2
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.palette :as palette]
            [lambdaisland.witchcraft.shapes :as shapes]
            [lambdaisland.witchcraft.matrix :as m]
            [net.arnebrasseur.cauldron.curves :as curves]))

(def me (wc/player "sunnyplexus"))

(comment
  (wc/fly! me)

  (wc/teleport me {:x 864.6187476329552, :y 423.6749086984752, :z 579.940400207871, :pitch 88.80000305175781, :yaw 38.39960861206055, :world "world"})
  (wc/teleport me {:x 864.6187476329552, :y 192.6800617246491, :z 579.940400207871, :pitch 43.94998550415039, :yaw -133.3503875732422, :world "world"})
  (wc/teleport me {:x 946.149706083101, :y 132.31526096779825, :z 414.3549097998223, :pitch 15.900014877319336, :yaw -2.249969482421875, :world "world"}))

(def anchor [883 95 468])

(def points
  [[40 0 200]
   [90 0 170]
   [30 0 140]

   [110 0 100]

   [80 0 25]

   [110 0 10]
   [70 0 -15]
   [45 0 25 ]
   [20 0 0]

   [0 0 40]
   [-60 0 -20]
   [-80 0 20]
   [-100 0 0]])

(defn midpoint-slope
  "Given a curve in the horizontal plane, vary its y-value so that it slopes down
  in the middle, and up towards either end. `:section-cnt` is the number of
  sections the curve is split into, with each section getting a y-value that's
  one below or above the previous, so the total curve will vary its y-value
  by `(/ section-cnt 2)`."
  [{:keys [section-cnt]} blocks]
  (let [block-cnt (count blocks)
        section-cnt 25
        sections (partition-all (Math/round (double (/ block-cnt section-cnt))) blocks)]
    (for [[blocks section] (map vector sections (range))
          [x _ z] blocks]
      [x
       (+ 4 (Math/abs (double (- section (/ section-cnt 2)))))
       z])))

(defn with-offset [offset blocks]
  (map #(wc/add % offset) blocks))

(defn extend-down
  "Extrude a set of blocks down to ground level"
  [blocks]
  (mapcat
   (fn [block]
     (let [hb (wc/highest-block-at block)]
       (if (< (wc/y hb) (wc/y block))
         (for [y (range (inc (wc/y hb)) (inc (wc/y block)))]
           [(wc/x block) y (wc/z block)])
         [block])))
   blocks))

(defn with-ys [ys blocks]
  (for [block blocks
        y ys]
    (wc/add block [0 y 0])))

(def section-count 25)

(defn sloped-curve [curve-number sections points]
  (->> points
       curves/bspline-nodes
       (curves/parallel-nodes curve-number)
       (curves/connect-segments)
       (midpoint-slope {:section-cnt sections})))

(defn foundation [points curve-number]
  (->> points
       (sloped-curve curve-number section-count)
       (with-offset anchor)
       extend-down))

(defn main-walls [points curve-number height]
  (->> points
       (sloped-curve curve-number section-count)
       (with-ys (range 1 height))
       (with-offset anchor)))

(defn flooring [{:keys [from to]} points]
  (let [a (partition 2 1 (sloped-curve from section-count points))
        b (partition 2 1 (sloped-curve to section-count points))]
    (distinct
     (for [[[a1 a2] [b1 b2]] (map vector a b)
           [start end] [[a1 b1] [a1 b2] [a2 b1] [a2 b2]]
           block (shapes/line {:start start :end end})]
       block))))

(wc/set-blocks (foundation points 0) {:material :stone-bricks})
(wc/set-blocks (foundation points -11) {:material :stone-bricks})
(wc/set-blocks (main-walls points -1 13) {:material :deepslate-bricks})
(wc/set-blocks (main-walls points -10 13) {:material :deepslate-bricks})

(wc/set-blocks
 (with-offset anchor (flooring {:from -2 :to -9} points))
 {:material :dark-oak-slab})

(wc/set-blocks
 (with-offset [0 10 0]
   (with-offset anchor (flooring {:from -2 :to -9} points)))
 {:material :cobbled-deepslate})

(wc/undo!)
(curves/bspline-nodes points)
