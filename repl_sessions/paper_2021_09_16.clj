(ns paper-2021-09-16
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.shapes :as shapes]
            [lambdaisland.witchcraft.events :as e]
            [lambdaisland.witchcraft.cursor :as cursor]
            [lambdaisland.witchcraft.palette :as p]
            [lambdaisland.witchcraft.matrix :as m]))

(require '[net.arnebrasseur.cauldron :as c])

(def me (wc/player "sunnyplexus"))

(wc/set-game-rules "world" {:do-daylight-cycle false
                            :do-weather-cycle false})

(wc/fly! me)
(mapv long (wc/xyz me))
[683 82 -808]
(wc/teleport me {:x 751 :y 70 :z -764 :pitch -8.699898 :yaw 144.60124})
(wc/teleport me [730 90 -808])
(wc/teleport me [683 82 -808])

(wc/undo!)

(defn rounded [coll]
  (into (empty coll)
        (map
         (partial mapv #(if (number? %)
                          (Math/round ^double %)
                          %)))
        coll))

(defn subtract [x y]
  (let [round (partial mapv #(Math/round ^double %))]
    (remove (fn [v]
              (some #{(round v)} (map round y)))
            x)))

(defn with-material [m coll]
  (map #(conj % (if (fn? m)
                  (m %)
                  m))
       coll))

(defn build [{:keys [small-ball-material
                     big-ball-material
                     corridor-material
                     unbuild?
                     material]
              :or {small-ball-material :blue-stained-glass
                   big-ball-material :green-stained-glass
                   corridor-material :sea-lantern}}]
  (let [small-ball-material (if unbuild? :air small-ball-material)
        big-ball-material (if unbuild? :air big-ball-material)
        corridor-material (if unbuild? :air corridor-material)
        fill (if unbuild? :air nil)
        bb-center [680 90 -848]
        sb-center [735 100 -800]
        small-ball (shapes/ball {:center sb-center :radius 19 :inner-radius 17.7})
        big-ball (shapes/ball {:center bb-center :radius 13 })
        small-ball-inside (shapes/ball {:center sb-center
                                        :radius 7.9
                                        :inner-radius 0})
        big-ball-inside (shapes/ball {:center bb-center
                                      :radius 11.9
                                      :inner-radius 0})
        corridor (-> (shapes/tube {:start (m/v- sb-center [0 0 0] #_[4 2 0])
                                   :end (m/v- bb-center [0 0 0]#_[-5 6 0])
                                   :radius 2.2
                                   :inner-radius 1.2
                                   :distance-fn m/chebyshev})
                     (subtract small-ball-inside)
                     (subtract big-ball-inside))
        corridor-inner (shapes/tube {:start (m/v- sb-center [4 2 0])
                                     :end (m/v- bb-center [-5 6 0])
                                     :radius 1.3
                                     :inner-radius 0
                                     :distance-fn m/chebyshev})]
    #_(map #(conj % :air)
           (concat big-ball-inside
                   small-ball-inside
                   (subtract big-ball corridor-inner)
                   (subtract small-ball corridor-inner)
                   corridor))
    (concat
     (with-material big-ball-material
       (subtract big-ball corridor-inner))
     (with-material small-ball-material
       (subtract small-ball corridor-inner))
     (with-material corridor-material
       corridor))
    ))

(wc/set-blocks
 (sequence
  (comp
   #_(map #(assoc % 3 :air)))
  (build {:big-ball-material
          (fn [[x y z]]
            (ball-gen (- y 80)))
          :corridor-material
          (fn [[x y z]] (corridor-gen (- x 690)))
          :small-ball-material
          (fn [[x y z]]
            (ball-gen (- y 85)))})))

(def corridor-gen
  (p/gradient-gen {:palette [:white-terracotta
                             :raw-iron-block
                             :polished-granite
                             :nether-quartz-ore
                             :netherrack]
                   :bleed-distance 4
                   :bleed 0.8
                   :spread 8}))

;; second corridor
(wc/set-blocks
 (-> (shapes/tube {:start [734 100 -781]
                   :end [733 120 -761]
                   :radius 2.2
                   :inner-radius 1.2
                   :distance-fn m/chebyshev
                   :material
                   (fn [[x y z]] (corridor-gen (+ z 760)))
                   :fill :air})
     ))




(def ball-gen
  (p/gradient-gen {:palette [:bedrock
                             :deepslate-bricks
                             :polished-deepslate
                             :deepslate
                             :polished-basalt
                             #_:gravel
                             :stone-bricks
                             :cracked-stone-bricks
                             :stone]}))

(wc/add-inventory me :firework-rocket 64
                  )


(keys wc/materials)

(xxx 24.80)
(def xxx
  )

(let [x 24.805
      idx 4
      dist (long (Math/abs (- (* 5 idx) x)))]
  [idx x])

(get [] 1)

(build {:material :redstone-block})
(wc/undo!)
(defn build-flooring []
  (wc/set-blocks
   (map #(conj (wc/xyz %) :spruce-slab) (flooring [693 76 -806])))

  (wc/set-blocks
   (map #(conj (wc/xyz %) :spruce-slab) (flooring [725 87 -811]))))

(wc/set-blocks
 (map #(conj (wc/xyz %) :spruce-slab) (flooring [744 117 -759])))

(p/palette-generator )

(p/material-gradient :oak-log :netherrack 40)

(let [[x y z](wc/xyz (wc/in-front-of me 10))]
  (map-)
  )

(-> (cursor/start [684 84 -794] :south)
    (cursor/pattern
     [:bedrock
      :deepslate-bricks
      :polished-deepslate
      :deepslate
      :polished-basalt
      #_:gravel
      :stone-bricks
      :cracked-stone-bricks
      :stone])
    cursor/build!)
(-> (cursor/start [683 84 -794] :south)
    (cursor/pattern
     [:white-terracotta
      :raw-iron-block
      :polished-granite
      :nether-quartz-ore
      :netherrack
      ])
    cursor/build!)

(p/distance :polished-basalt :gravel)


(defn flooring [start]
  (remove #{start}
          (map wc/xyz
               (c/fill start {:pred #(= (wc/material-name %) :air)}))))

(def fff (flooring [674 83 -855]))

(wc/set-blocks (with-material :dark-oak-planks
                 fff))

(wc/undo!)

(e/listen! :player-interact ::print-block
           (fn [{:keys [clickedBlock]}]
             (when clickedBlock
               #_(prn (mapv long (wc/xyz clickedBlock))))))

(let [start (atom nil)]
  (e/listen! :player-interact ::line-builder
             (fn [{:keys [clickedBlock item] :as e}]
               #_(when (and item
                            clickedBlock)
                   (case (wc/material-name item)
                     :wooden-axe
                     (do
                       (prn "Line start: " (wc/loc clickedBlock))
                       (reset! start (wc/location clickedBlock)))
                     :diamond-axe
                     (when @start
                       (prn "Line end: " (wc/loc clickedBlock))
                       (wc/set-blocks
                        (shapes/line {:start @start
                                      :end (wc/location clickedBlock)
                                      :material :stone})))
                     nil)
                   #_(prn (mapv long (wc/xyz clickedBlock)))))))

(def pos [772 68 -730])

(defn set-blocks [blocks]
  (wc/set-blocks (map #(m/v+ % pos) blocks)))

(wc/undo!)

(wc/eye-location me)

(x)
(e/listen!
 :player-interact ::capture-block
 (fn [e]
   (when (:clickedBlock e)
     (prn (mapv long (wc/xyz (:clickedBlock e)))))))

(wc/set-blocks
 )

(wc/set-blocks
 )

(defn span-square [blocks]
  (for [x (range (apply min (map wc/x blocks))
                 (inc (apply max (map wc/x blocks))))
        y (range (apply min (map wc/y blocks))
                 (inc (apply max (map wc/y blocks))))
        z (range (apply min (map wc/z blocks))
                 (inc (apply max (map wc/z blocks))))]
    [x y z]))

(wc/set-blocks
 (with-material :spruce-planks
   (mapcat
    span-square
    (vals
     (group-by second
               (rounded
                (concat
                 (shapes/line {:start[657 69 -878]
                               :end [673 84 -854]
                               })
                 (shapes/line {:start[647 69 -870]
                               :end [673 84 -854]
                               }))))))))
(wc/undo!)

(keys wc/materials)

(wc/set-blocks
 [(conj (wc/xyz (wc/in-front-of me 3)) :lava)])

(wc/set-fly-speed me 0.3,)
(wc/allow-flight me false)

(wc/undo!)
