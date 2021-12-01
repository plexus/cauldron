(ns advent-20211201
  (:require [lambdaisland.witchcraft :as wc]))

(def me (wc/player "sunnyplexus"))

(wc/loc me)
(wc/xyz me)
;; => [1276.8381411042578 72.0 515.8139973199625]

(wc/time)

(wc/set-game-rule (wc/world "world")
                  :do-daylight-cycle false)

(wc/fly! )

(wc/fly-speed me)
(wc/set-fly-speed me 0.5)

(def anchor [1276 72 515])

(wc/set-block anchor :spruce-log)

(wc/set-blocks
 (map #(wc/add % anchor)
      (remove nil?
              (for [x (range -8 8)
                    y (range 25)
                    z (range -8 8)]
                (cond
                  (= x z 0)
                  [x y z :spruce-log]

                  (and (< 4 y)
                       (< (wc/distance [x 0 z] [0 0 0]) (/ (- 30 y) 5)))
                  [x y z :spruce-leaves])))))

(wc/undo!)
