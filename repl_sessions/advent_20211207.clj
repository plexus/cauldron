(ns advent-20211207
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


(wc/add-inventory (wc/player) :diamond-shovel)

(map :material (wc/inventory me))


(wc/set-blocks
 (map #(assoc (dissoc (wc/block %) :block-data) :material :oak-planks)
      (fill/fill-xz (wc/target-block me)
                    {:pred #(not= :stripped-birch-wood (wc/mat %))})))

(wc/undo!)

(wc/add-inventory me :chain 64)
(wc/add-inventory me :dark-oak-log 64)

(wc/set-blocks
 (map #(assoc (dissoc (wc/block %) :block-data) :material :air)
      (fill/fill-xyz (wc/target-block me)
                     {:pred #(not= :stripped-birch-wood (wc/mat %))})))


(map #(m/v- % [-16355 122 -17279])
     (map wc/blockv
          (fill/fill-xyz (wc/target-block me)
                         {:throw? false
                          :limit 2})))

(def lantern-bar
  [
   [3 0 0 :chain]
   [0 0 0 :chain]
   [3 -1 0 :chain]
   [0 -1 0 :chain]

   [-1 -3 0 :lantern]
   [1 -3 0 :lantern]
   [3 -3 0 :lantern]

   [-2 -2 0 :dark-oak-log {:axis :x}]
   [-1 -2 0 :dark-oak-log {:axis :x}]
   [0 -2 0 :dark-oak-log {:axis :x}]
   [1 -2 0 :dark-oak-log {:axis :x}]
   [2 -2 0 :dark-oak-log {:axis :x}]
   [3 -2 0 :dark-oak-log {:axis :x}]
   [4 -2 0 :dark-oak-log {:axis :x}]

   ])

(range -3 4 3)
(quot 9 2)

(* 0.5 (/ 9 4))
0 1

(wc/fly! me)
(wc/teleport me anchor)

(def anchor {:x -16385, :y 149, :z -17236, :material :air})
(let [anchor ]

  (wc/set-blocks (lantern-bar-gen {:chain-width 3
                                   :bar-width 11
                                   :lanterns 5
                                   :chain-length 8
                                   :axis :z})
                 {:start
                  (wc/add [13 18 44]
                          anchor)}

                 ))

(wc/set-blocks (lantern-bar-gen {:chain-width 3
                                 :bar-width 11
                                 :lanterns 5
                                 :chain-length 8
                                 :axis :z})
               {:start
                (wc/target-block me)                })

(wc/undo!)

(wc/block-data (wc/target-block me))

(wc/break-naturally (wc/target-block me))

(def pos (atom
          (wc/target-block me)))

(do
  (swap! pos wc/add [(rand-nth [0 0 -1 1])
                     (rand-nth [0 0 -1 1])
                     -1])
  (run! #(wc/break-naturally % :diamond-axe)
        (shapes/ball
         {:center @pos
          :radius 2.5
          :inner-radius -1
          }))
  (wc/set-block (assoc (wc/add @pos [0 1 0])
                       :block-data {:hanging true}) :lantern))
