(ns cauldron-2021-07-13
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as c]
            [net.arnebrasseur.cauldron :refer :all]
            [net.arnebrasseur.cauldron.layout :refer :all]
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
(wc/time)
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

;; Build our stairs, add some randomness
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
    (c/reps 5 (comp (steps/zigzag 3)
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

;; Add some fences around the path
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

;; set some glowstones on top of the fences fences around the path
(wc/set-blocks
 (for [fence fences]
   (if (= 1 (count (set (concat (neighbours fence {:dy [0 1]
                                                   :dz [0]})
                                (neighbours fence {:dx [0]
                                                   :dy [0 1]})))))
     (assoc (update fence :y inc) :material :glowstone))))

;; The location of the pillars of the house, topmost block of each pillar (flush
;; with the future floor)
(def foundation
  [{:x 2255 :y 114 :z 174}
   {:x 2246 :y 114 :z 174}
   {:x 2246 :y 114 :z 166}
   {:x 2241 :y 114 :z 166}
   {:x 2241 :y 114 :z 155}
   {:x 2253 :y 114 :z 155}
   {:x 2253 :y 114 :z 161}
   {:x 2255 :y 114 :z 161}
   {:x 2255 :y 114 :z 165}])

;; Build fence all around
(c/build!
 (reduce
  (fn [c {:keys [a dir length]}]
    (-> c
        (merge a)
        (c/face :down)
        (c/material :log 2)
        (c/block)
        (c/material :fence)
        (c/steps (dec length) dir)))
  (c/start)
  (points->path (map #(update % :y inc) foundation))))

;; Put in more flooring
(wc/set-blocks
 (filter #(#{:air :snow} (:material (wc/block %)))
         (map #(assoc % :material :step :data 10)
              (path->plane (points->path foundation)))))

(defn with-material [coll mat]
  (map #(assoc % :material mat) coll))

(defn update-all [coll kw f & args]
  (into (empty coll)
        (map #(apply update % kw f args))
        coll))

(def foundation-plane (-> foundation points->path path->plane))

(def pillar-locs (-> foundation-plane
                     shrink-plane
                     plane->corners
                     (with-material :log)))

(def wall-outline (-> foundation-plane
                      shrink-plane
                      shrink-plane
                      plane->outline
                      (with-material :wood)))

(def wall-path (outline->path wall-outline))

(wc/set-blocks
 (mapcat #(update-all pillar-locs :y + %)
         (range 1 5)))

;; walls
(wc/set-blocks
 (apply concat
        (for [{:keys [a length dir]} wall-path
              y (range 1 5)]
          (let [window-len (long (/ length 3))
                part1-len (long (/ (- length window-len) 2))
                part2-len (- length window-len part1-len)]
            (if (#{2 3} y)
              (-> (c/start a dir)
                  (update :y + y)
                  (c/material :wood)
                  (c/steps part1-len)
                  (c/material :thin-glass)
                  (c/steps window-len)
                  (c/material :wood)
                  (c/steps part2-len)
                  :blocks)
              (-> (c/start a dir)
                  (update :y + y)
                  (c/material :wood)
                  (c/steps length)
                  :blocks))))))


;; roof
(loop [y 5
       path (grow-path (grow-path wall-path))]
  (as-> (c/start (:a (first path)) (:dir (first path))) $
    (assoc $ :rotate-block 4)
    (update $ :y + y)
    (c/material $ :dark-oak-stairs)
    (c/block $)
    (reduce (fn [c {:keys [dir length]}]
              (-> c
                  (c/steps length dir)))
            $
            path)
    (c/build! $))
  (when (< y 9)
    (recur (inc y) (shrink-path path))))

(wc/add-inventory  (me) :dark-oak-door)

(wc/set-block (assoc (last-block) :material :dark-oak-stairs :data 0))

(wc/undo!)

(wc/fly!)

(time (plane->path
       (wc/block-set
        (concat
         (for [x (range 3)
               y [0]
               z (range 3)]
           {:x x :y y :z z})
         (for [x (range 10 13)
               y [0]
               z (range 10 13)]
           {:x x :y y :z z})))))
