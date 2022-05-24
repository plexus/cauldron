(ns repl-sessions.s2022-05-21
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.palette :as palette]
            [lambdaisland.witchcraft.matrix :as m]
            [net.arnebrasseur.cauldron.curves :as curves]))

(def me (wc/player "sunnyplexus"))
(wc/set-time 1000)
(wc/clear-weather)

(wc/fly! me)

(def anchor [883 94 468])


(wc/set-blocks
 (for [x (range -10 6)
       z (range 0 11)]
   (wc/add [0 1 0 (cond
                    (= 0 x z)                         :yellow-wool
                    ((set [ (mod x 5) (mod z 5) ]) 0) :red-wool
                    :else                             :pink-wool)]
           (wc/highest-block-at (wc/add anchor [(* 20 x) 0 (* 20 z)])))))

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

(wc/undo!)

(wc/set-blocks
 (concat
  (for [[x y z m] points]
    [x 20 z (or m :orange-concrete)])
  (curves/bspline points {:y 19 :material :blue-wool}))
 {:anchor anchor})

(do
  (defn wall-palette []
    (palette/rand-palette {
                           ;; :bricks 0.20
                           ;; :granite 0.40
                           ;; :polished-granite 10
                           ;; :waxed-exposed-cut-copper 13
                           ;; :waxed-exposed-copper 3
                           ;; :spruce-planks 5
                           ;; :raw-copper-block 1
                           ;; :stripped-spruce-wood 3

                           :cobbled-deepslate 2.5
                           :deepslate-coal-ore 1
                           :cracked-deepslate-bricks 1
                           :chiseled-deepslate 0
                           :basalt 0

                           ;; :cyan-terracotta 1
                           ;; :deepslate-bricks 2
                           ;; :gray-glazed-terracotta 1
                           ;; :cobblestone 0
                           ;; :polished-andesite 0

                           ;; :mossy-stone-bricks 3
                           ;; :stone-bricks 1

                           ;; :mossy-cobblestone 5
                           :orange-stained-glass 0.5
                           })
    )

  (wc/set-blocks
   (let [spline-blocks (curves/bspline points)]
     (for [[x _ z] spline-blocks
           y (range 1)]
       (wc/add [0 (inc y) 0 (wall-palette) (rand-nth [:east :west :north :south])]
               (wc/highest-block-at (wc/add anchor [x y z]))))
     )))

(wc/undo!)

(wc/set-blocks
 (for [[x z] (curves/bspline-nodes points)]
   [x 20 z :blue-wool])
 {:anchor anchor}
 )
(curves/mp-seq (curves/multipath 2 (curves/bspline* points nil)))


(wc/set-blocks
 (for [[[x1 z1] [x2 z2]] (partition 2 1 (curves/bspline-nodes points))
       :when (not= [x1 z1] [x2 z2])]
   (conj
    (m/v+ [x1 20 z1]
          (matrix/cross-product (m/vnorm (m/v- [x2 0 z2] [x1 0 z1]))
                                [0 -8 0]))
    :red-wool))
 {:anchor (conj anchor :red-wool)})

(count (distinct (curves/bspline-nodes points)))

(count
 (for [[[x1 z1] [x2 z2]] (partition 2 1 (curves/bspline-nodes points))
       :when (not= [x1 z1] [x2 z2])]
   (conj
    (m/v+ [x1 20 z1]
          (matrix/cross-product (m/vnorm (m/v- [x2 0 z2] [x1 0 z1]))
                                [0 -8 0]))
    :red-wool)))

(wc/set-blocks
 (curves/connect-segments
  (curves/parallel-nodes (curves/bspline-nodes points) -1.7)
  {:material :smooth-basalt
   :y 10})
 {:anchor anchor}
 )

(wc/set-blocks
 (for [[x z] #_(curves/bspline-nodes points)
       (curves/parallel-nodes (curves/bspline-nodes points) -2.08)]
   [x 11 z :green-wool])
 {:anchor anchor}
 )

(wc/undo!)
