(ns advent-20211208
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as cursor]
            [lambdaisland.witchcraft.events :as events]
            [lambdaisland.witchcraft.fill :as fill]
            [lambdaisland.witchcraft.markup :as markup]
            [lambdaisland.witchcraft.palette :as palette]
            [lambdaisland.witchcraft.shapes :as shapes]
            [lambdaisland.witchcraft.matrix :as m]))

(def me (wc/player "sunnyplexus"))
(wc/fly! me)

(wc/break-naturally (wc/target-block me))

(def pos (atom (wc/target-block me)))

(do
  (swap! pos wc/add [(rand-nth [0 0 0 -1 1])
                     (rand-nth [0 0 0 -1 1])
                     -1])
  (run! #(wc/break-naturally % :diamond-pickaxe)
        (remove
         (comp
          #{:lantern}
          :material
          wc/block)
         (shapes/ball
          {:center @pos
           :radius 2.5
           :inner-radius -1})))
  (when (= 0 (rand-int 3))
    (wc/set-block (assoc (wc/add @pos [0 1 1])
                         :block-data {:hanging true})
                  :lantern)))

(take 20 (palette/neighbors :red-concrete))

(wc/set-block (dissoc (wc/target-block me) :block-data) :netherrack)
