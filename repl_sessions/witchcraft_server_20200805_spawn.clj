(ns witchcraft-server-20200805-spawn
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as c]))

(defn me []
  (wc/player "sunnyplexus"))

(wc/set-time 0)
(wc/fly! (me))

(defonce block-history (atom ()))

(wc/listen! :player-interact ::capture-block
            (fn [e]
              (when (:clickedBlock e)
                (swap! block-history conj (:clickedBlock e)))))


(wc/block (first @block-history))

(-> (c/start [-46 62 -125] :west)
    (c/material :wood-step 8)
    (c/reps 2
            (fn [c]
              (-> c
                  (c/excursion #(c/steps % 7))
                  (c/move 1 :right))))
    (c/material :wood-stairs 2)
    (c/move 1 :up)
    (c/steps 7)
    (c/move 3 :left 1 :forward)
    (c/rotate 4)
    (c/steps 7)
    (c/build!))

(-> (c/start [-46 62 -125] :west)
    (c/material :fence)
    (c/move 6 :forward 1 :down)
    (c/face :down)
    (c/steps 3)
    (c/build!)
    )

(wc/undo!)

(wc/set-blocks
 (for [x (range -52 -47 2)
       y (range 55 63)
       z [-124 -127]
       :when (#{:water :stationary-water} (:material (wc/block [x y z])))]
   {:x x
    :y y
    :z z
    :material :fence}))

(map wc/block (take 2 @block-history))

(for [x (range -52 -47 2)
      y (range 55 62)
      z [-124 -127]
      ]
  (wc/block [x y z])
  )

(wc/undo!)

(wc/add-inventory (me) :diamond-spade)
