(ns repl-sessions.s2022-05-24
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.palette :as palette]
            [lambdaisland.witchcraft.shapes :as shapes]
            [lambdaisland.witchcraft.matrix :as m]
            [net.arnebrasseur.cauldron.curves :as curves]
            [net.arnebrasseur.better-for :refer [for*]]))

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

(defn main-walls [points {:keys [curve-number height
                                 segment-size
                                 arch-width
                                 arch-height
                                 arch-overlap]}]
  (let [curve (sloped-curve curve-number section-count points)
        arch-mwidth (/ arch-width 2)
        arch-base-height (- arch-height arch-mwidth)]
    (with-offset anchor
      (for [segment (partition segment-size curve)
            :let [[mx my mz] (nth segment (Math/ceil (double (/ (- segment-size arch-overlap) 2))))
                  [nx ny nz] (nth segment (Math/floor (double (/ (+ segment-size arch-overlap) 2))))]
            [[x yy z] idx] (map list segment (range))
            y (range 1 height)
            :let [point [x (+ yy y) z]]
            :when (not
                   (or
                    (and (< y arch-base-height)
                         (< (/ (- segment-size arch-width) 2)
                            idx
                            (/ (+ segment-size arch-width) 2)))
                    (and (< (wc/distance point [mx arch-base-height mz])
                            (+ arch-mwidth (/ arch-overlap 2)))
                         (< (wc/distance point [nx arch-base-height nz])
                            (+ arch-mwidth (/ arch-overlap 2))))
                    ))]
        point))))

(filter #(< 3 % 12) (range 15))

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
(wc/set-blocks (main-walls points
                           {:height 20
                            :segment-size 25
                            :curve-number -1
                            :arch-width 7.5
                            :arch-height 15
                            :arch-overlap 2}) {:material :deepslate-bricks})
(wc/set-blocks (main-walls points -10 13) {:material :deepslate-bricks})

;; (wc/set-blocks
;;  (with-offset anchor (flooring {:from -2 :to -9} points))
;;  {:material :dark-oak-slab})

;; (wc/set-blocks
;;  (with-offset [0 10 0]
;;    (with-offset anchor (flooring {:from -2 :to -9} points)))
;;  {:material :cobbled-deepslate})

;; (wc/undo!)


;;;; ARCH...


(def arch-anchor [841.0 122.0 687.0])

(wc/teleport arch-anchor)

(wc/set-blocks
 (for [x (range -5 6)
       y (range 6)
       z [0]
       :when (< 4.5 (m/vlength [x y z]) 5.5)]
   [x y z :stone])
 {:anchor arch-anchor})

(wc/undo!)

(let [radius 9
      resolution 1
      palette (cycle [:green-terracotta :lime-terracotta :yellow-terracotta :orange-terracotta]) ]
  (wc/set-blocks
   (for [x (range (- (dec radius)) radius)
         y (range radius)
         z [0]]
     [x y z
      (nth palette
           (Math/round
            (* resolution (m/vlength [x y z]))))])
   {:anchor arch-anchor}))

(defn arch-shape
  "Creates an arch shape in the xy-plane, with given width and height. width can
  not be smaller than height. if (= width height) then this is just a half
  circle, if (< width height) then there a certain amount of 'pointedness' at
  the top. Note that if (< width height) the actual height might be smaller than
  the requested height."
  [{:keys [width height]}]
  (assert (<= width height))
  (let [pointedness (- height width)
        radius (+ width pointedness)]
    (for [x (range (- (dec radius)) radius)
          y (range radius)
          z [0]
          :let [n (Math/round
                   (max (wc/distance [pointedness 0 0] [x y z])
                        (wc/distance [(- pointedness) 0 0] [x y z])))]
          :when (= (dec radius) n)]
      [x y z])))

(wc/set-blocks (arch-shape {:width 12 :height 19})
               {:anchor arch-anchor
                :material :chiseled-red-sandstone})

(wc/set-blocks (m/transform
                (arch-shape {:width 12 :height 19})
                (m/rotation-matrix (/ Math/PI 2) :x :z))
               {:anchor arch-anchor
                :material :chiseled-red-sandstone})

(let [steps 60]
  (wc/set-blocks (for [x (range (- steps) steps)
                       blk (m/transform
                            (arch-shape {:width 12 :height 19})
                            (m/rotation-matrix (* Math/PI (/ 1 steps) x) :x :z))]
                   blk)
                 {:anchor arch-anchor
                  :material :lime-stained-glass}))

(wc/add-inventory me :torch 64)

(wc/set-blocks (m/transform
                (arch-shape {:width 12 :height 19})
                (m/rotation-matrix (/ Math/PI 2) :x :z))
               {:anchor arch-anchor
                :material :chiseled-red-sandstone})


(wc/set-blocks
 (m/extrude (conj (remove #{[0 17 0]}
                          (arch-shape {:width 12 :height 19}))
                  [0 17 0 :shroomlight])
            [0 -0.34 -1]
            53)
 {:anchor arch-anchor
  :material :chiseled-red-sandstone})

(arch-shape {:width 5 :height 5})

(wc/set-blocks (m/extrude (concat (for [w (range 9 13)
                                        b (shapes/arch-shape {:width w :height (+ w 5)
                                                              :material (fn [[x y z]]
                                                                          (if (= 0 (mod x 5))
                                                                            :sea-lantern
                                                                            :slime-block))})]
                                    b)
                                  (shapes/line {:start [-11 0 0]
                                                :end [11 0 0]
                                                :material :bottom}))
                          [0 -0.1 -1]
                          300)
               {:anchor (wc/add arch-anchor [0 10 0])
                :palette (fn [m]
                           (if (= m :bottom)
                             (palette/rand-palette {:blue-ice 20
                                                    :honey-block 1})
                             m))}
               )

(wc/add-inventory me :birch-boat)
