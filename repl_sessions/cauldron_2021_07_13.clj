(ns cauldron-2021-07-13
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as c]
            [net.arnebrasseur.cauldron :refer :all]
            [net.arnebrasseur.cauldron.step-fns :as steps]
            [net.arnebrasseur.cauldron.roll-tables :as rolls]
            [net.arnebrasseur.cauldron.structures :as struct]))

(wc/start! {:level-seed "witchcraft"})
(wc/fly!)

(capture-blocks!)

;; Initial starting location. Pretty cool island but very island, lots of ocean
(def island-loc [27.058644734325476 86.0 82.51919827689997])

;; Found a mountain that I'm gonna turn into a mesa
(def mesa-loc [2255.651103806431 110.0 164.30000001192093])
(def mesa-overview-loc [2181.9006158178167 120.15193543982032 180.17158041016833 270.29968 21.000025])

(wc/teleport (me) mesa-overview-loc)

(clear-surface! [2250 115 161])

(wc/set-blocks
 (for [y [106 105]
       [x y z] (map locv (fill [2256 y 173]))
       :let [topping (wc/block [x (inc y) z])]
       :when (= :snow (wc/material-name topping))]
   {:x x :y (inc y) :z z :material :air}))

(wc/set-blocks
 (for [y [106 105]
       [x y z] (map locv (fill [2256 y 173]))
       :let [topping (wc/block [x (inc y) z])]
       :when (= :air (wc/material-name topping))
       :let [[m d] (rolls/stone-paving)]]
   {:x x :y y :z z :material m :data d}))


(defn zigzag [width]
  (fn [c]
    (let [s (dec width)]
      (-> c
          (c/step)
          (c/rotate -2)
          (c/steps s)
          (c/rotate 2)
          (c/step)
          (c/rotate 2)
          (c/steps s)
          (c/rotate -2)))))


(-> (c/start [2255 114 159] :east)
    (assoc :block-fn steps/clear-above
           :step-fn (fn [c dir]
                      (let [c (c/step-fn c dir)]
                        (if (= :east dir)
                          (if (< (rand-int 10) 7)
                            (assoc (update c :y dec)
                                   :material :cobblestone-stairs
                                   :data 1)
                            (assoc c :material :step :data 3))
                          c)))
           :material :cobblestone-stairs
           :data 1
           :face-direction? false)
    (c/reps 5 (comp (zigzag 3)
                    (fn [c]
                      (let [n (rand-int 10)]
                        (cond
                          (< n 2)
                          (update c :z inc)
                          (< n 4)
                          (update c :z dec)
                          :else
                          c)))))
    (c/build))

(def stairs (remove (comp #{:air} :material) (map wc/block (first @wc/undo-history))))

[{:x 2255.0, :y 114.0, :z 157.0, :material :cobblestone-stairs, :data 1}
 {:x 2255.0, :y 114.0, :z 158.0, :material :cobblestone-stairs, :data 1}
 {:x 2255.0, :y 114.0, :z 159.0, :material :cobblestone-stairs, :data 1}
 {:x 2256.0, :y 113.0, :z 157.0, :material :cobblestone-stairs, :data 1}
 {:x 2256.0, :y 113.0, :z 158.0, :material :cobblestone-stairs, :data 1}
 {:x 2256.0, :y 113.0, :z 159.0, :material :cobblestone-stairs, :data 1}
 {:x 2257.0, :y 112.0, :z 156.0, :material :cobblestone-stairs, :data 1}
 {:x 2257.0, :y 112.0, :z 157.0, :material :cobblestone-stairs, :data 1}
 {:x 2257.0, :y 112.0, :z 158.0, :material :cobblestone-stairs, :data 1}
 {:x 2258.0, :y 111.0, :z 156.0, :material :cobblestone-stairs, :data 1}
 {:x 2258.0, :y 111.0, :z 157.0, :material :cobblestone-stairs, :data 1}
 {:x 2258.0, :y 111.0, :z 158.0, :material :cobblestone-stairs, :data 1}
 {:x 2259.0, :y 110.0, :z 156.0, :material :cobblestone-stairs, :data 1}
 {:x 2259.0, :y 110.0, :z 157.0, :material :cobblestone-stairs, :data 1}
 {:x 2259.0, :y 110.0, :z 158.0, :material :cobblestone-stairs, :data 1}
 {:x 2260.0, :y 109.0, :z 156.0, :material :cobblestone-stairs, :data 1}
 {:x 2260.0, :y 109.0, :z 157.0, :material :cobblestone-stairs, :data 1}
 {:x 2260.0, :y 109.0, :z 158.0, :material :cobblestone-stairs, :data 1}
 {:x 2261.0, :y 109.0, :z 157.0, :material :step, :data 3}
 {:x 2261.0, :y 109.0, :z 158.0, :material :step, :data 3}
 {:x 2261.0, :y 109.0, :z 159.0, :material :step, :data 3}
 {:x 2262.0, :y 108.0, :z 157.0, :material :cobblestone-stairs, :data 1}
 {:x 2262.0, :y 108.0, :z 158.0, :material :cobblestone-stairs, :data 1}
 {:x 2262.0, :y 108.0, :z 159.0, :material :cobblestone-stairs, :data 1}
 {:x 2263.0, :y 107.0, :z 156.0, :material :cobblestone-stairs, :data 1}
 {:x 2263.0, :y 107.0, :z 157.0, :material :cobblestone-stairs, :data 1}
 {:x 2263.0, :y 107.0, :z 158.0, :material :cobblestone-stairs, :data 1}
 {:x 2264.0, :y 107.0, :z 156.0, :material :step, :data 3}
 {:x 2264.0, :y 107.0, :z 157.0, :material :step, :data 3}
 {:x 2264.0, :y 107.0, :z 158.0, :material :step, :data 3}]

(wc/set-blocks (for [[x blocks] (group-by :x stairs)]
                 (let [min-z (apply min (map :z blocks))
                       max-z (apply max (map :z blocks))
                       y (:y (first blocks))]
                   (if (#{:air :snow} (:material (wc/block [x y (inc max-z)])))
                     {:x x :y y :z (inc max-z) :material :fence}
                     {:x x :y (inc y) :z (inc max-z) :material :fence}))))

(wc/set-blocks
 (for [[x blocks] (group-by :x stairs)]
   (let [min-z (apply min (map :z blocks))
         max-z (apply max (map :z blocks))
         y (:y (first blocks))]
     (if (#{:air :snow} (:material (wc/block [x (dec y) min-z])))
       {:x x :y y :z min-z :material :air}))))

(def stairs (remove (comp #{:air} :material) (map wc/block stairs)))

(wc/set-blocks
 (apply concat
        (for [[x blocks] (group-by :x stairs)]
          (let [min-z (apply min (map :z blocks))
                max-z (apply max (map :z blocks))
                y (inc (:y (first blocks)))]
            (loop [x x y y z (dec min-z)
                   blocks [{:x x :y y :z z :material :fence}]]
              (let [y (dec y)]
                (if (#{:air :snow} (:material (wc/block [x y z])))
                  (recur x y z (conj blocks {:x x :y y :z z :material :fence}))
                  blocks)))))))


(def fences
  (map wc/block (filter (comp #{:fence} wc/material-name) (fill (last-block) {:dy [0 1]}))))

[{:x 2255.0, :y 115.0, :z 156.0, :material :fence, :data 0}
 {:x 2256.0, :y 114.0, :z 160.0, :material :fence, :data 0}
 {:x 2262.0, :y 109.0, :z 160.0, :material :fence, :data 0}
 {:x 2261.0, :y 109.0, :z 160.0, :material :fence, :data 0}
 {:x 2260.0, :y 110.0, :z 159.0, :material :fence, :data 0}
 {:x 2257.0, :y 110.0, :z 155.0, :material :fence, :data 0}
 {:x 2262.0, :y 108.0, :z 156.0, :material :fence, :data 0}
 {:x 2257.0, :y 112.0, :z 155.0, :material :fence, :data 0}
 {:x 2257.0, :y 111.0, :z 155.0, :material :fence, :data 0}
 {:x 2262.0, :y 109.0, :z 156.0, :material :fence, :data 0}
 {:x 2257.0, :y 113.0, :z 155.0, :material :fence, :data 0}
 {:x 2261.0, :y 110.0, :z 157.0, :material :fence, :data 0}
 {:x 2260.0, :y 110.0, :z 156.0, :material :fence, :data 0}
 {:x 2261.0, :y 108.0, :z 157.0, :material :fence, :data 0}
 {:x 2260.0, :y 108.0, :z 156.0, :material :fence, :data 0}
 {:x 2263.0, :y 108.0, :z 159.0, :material :fence, :data 0}
 {:x 2261.0, :y 109.0, :z 157.0, :material :fence, :data 0}
 {:x 2260.0, :y 109.0, :z 156.0, :material :fence, :data 0}
 {:x 2255.0, :y 115.0, :z 160.0, :material :fence, :data 0}
 {:x 2259.0, :y 110.0, :z 156.0, :material :fence, :data 0}
 {:x 2259.0, :y 111.0, :z 156.0, :material :fence, :data 0}
 {:x 2259.0, :y 109.0, :z 156.0, :material :fence, :data 0}
 {:x 2258.0, :y 110.0, :z 156.0, :material :fence, :data 0}
 {:x 2258.0, :y 112.0, :z 156.0, :material :fence, :data 0}
 {:x 2257.0, :y 112.0, :z 159.0, :material :fence, :data 0}
 {:x 2258.0, :y 111.0, :z 156.0, :material :fence, :data 0}
 {:x 2258.0, :y 111.0, :z 159.0, :material :fence, :data 0}
 {:x 2259.0, :y 110.0, :z 159.0, :material :fence, :data 0}
 {:x 2263.0, :y 108.0, :z 155.0, :material :fence, :data 0}
 {:x 2256.0, :y 114.0, :z 156.0, :material :fence, :data 0}
 {:x 2263.0, :y 107.0, :z 155.0, :material :fence, :data 0}
 {:x 2264.0, :y 108.0, :z 155.0, :material :fence, :data 0}
 {:x 2264.0, :y 107.0, :z 155.0, :material :fence, :data 0}
 {:x 2264.0, :y 108.0, :z 159.0, :material :fence, :data 0}]

(wc/set-blocks
 (for [fence fences]
   (if (= 1 (count (set (concat (neighbours fence {:dy [0 1]
                                                   :dz [0]})
                                (neighbours fence {:dx [0]
                                                   :dy [0 1]})))))
     (assoc (update fence :y inc) :material :glowstone))))

(set (concat (neighbours (last-block) {:dy [0 1]
                                       :dz [0]})
             (neighbours (last-block) {:dx [0]
                                       :dy [0 1]})))

(neighbours (last-block))

(wc/undo!)
(wc/set-time 0)
(wc/add-inventory (me) [:stone 6] 1)

(.getData (first (wc/inventory (me))))

(wc/listen! :player-interact ::capture-block
            (fn [e]
              (when (:clickedBlock e)
                #_                (clear-surface! (:clickedBlock e))                )))
