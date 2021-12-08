(ns advent-20211204
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.palette :as palette]
            [lambdaisland.witchcraft.fill :as fill]
            [lambdaisland.witchcraft.shapes :as shapes]
            [lambdaisland.witchcraft.markup :as markup]
            [lambdaisland.witchcraft.cursor :as cursor]))

(def me (wc/player "sunnyplexus"))
(wc/fly! me)

(def mountain-spot [-16960.33844113195 116.0 -18054.772657936446])
(wc/teleport mountain-spot)

(wc/send-title me
               (markup/fAnCy "Mountain chalet" [:gold :white]))

(wc/send-title me
               [:bold [:gold "Implementing"]]
               [:bold [:gold "INTEROP"]])

(let [chars (cycle (next (markup/fAnCy "Mountain chalet --- " [:gold :white])))]
  ((fn marquee [i]
     (wc/send-title me (into [:<>] (take 19 (drop i chars))))
     (future
       (Thread/sleep 300)
       (when (< i (* 4 19))
         (marquee (inc i)))))
   0))

(defn roof-segment [cursor length]
  (-> cursor
      (cursor/excursion
       #(-> %
            (cursor/material :birch-planks)
            (cursor/steps 2)
            (cursor/material :cobbled-deepslate-slab {:type :top})
            (cursor/steps length)
            (cursor/material :birch-planks)
            (cursor/steps 2)))

      (cursor/rotate 2)
      (cursor/move 1 :forward)
      (cursor/rotate -2)

      (cursor/excursion
       #(-> %
            (cursor/rotation 6)
            (cursor/material :birch-stairs)
            (cursor/steps 2)
            (cursor/material :cobbled-deepslate-slab {:type :bottom})
            (cursor/steps length)
            (cursor/material :birch-stairs)
            (cursor/steps 2)))

      (cursor/rotate 2)
      (cursor/move 1 :forward 1 :down)
      (cursor/rotate -2)))

(defn half-roof [cursor {:keys [length sections]}]
  (-> cursor
      (cursor/reps sections roof-segment length)
      (cursor/move 1 :up)
      (cursor/material :birch-slab {:type :bottom})
      (cursor/steps (+ length 4))))

(defn roof-triangle [cursor {:keys [sections] :as opts}]
  (-> (reduce
       (fn [c sect]
         (cursor/excursion c #(-> %
                                  (cursor/move (dec (* sect 2)) :left
                                               sect :down
                                               1 :forward)
                                  (cursor/rotate 2)
                                  (cursor/steps (dec (* sect 4))))))
       (cursor/material cursor :dark-oak-wood)
       (range 1 sections))))

(defn roof [cursor {:keys [length sections] :as opts}]
  (-> cursor
      (cursor/excursion roof-triangle opts)
      (cursor/excursion half-roof opts)
      (cursor/move (+ length 3) :forward)
      (cursor/rotate 4)
      (cursor/excursion roof-triangle opts)
      (cursor/excursion half-roof opts)))

(-> (cursor/start [0 5 0] :west)
    (roof {:length 4 :sections 3})
    (cursor/build! {:start (wc/target-block me)}))

(wc/undo!)

(defn chalet-section [{:keys [height depth width direction]}]
  (concat
   (-> (cursor/start [0 (dec (+ height width)) 0] direction)
       (cursor/move 2 :backward)
       (roof {:length depth :sections width})
       :blocks)
   (let [dx -1
         dz (- (- (* width 2) 2))
         l (- (* width 4) 3)
         w (+ 2 depth)
         [dx dz l w] (if (#{:east :west} direction)
                       [dx dz l w]
                       [dz dx w l])]
     (shapes/rectube {:material :stripped-birch-wood
                      :start [dx 0 dz]
                      :length l
                      :width w
                      :height height
                      }))))

(def anchor {:x -16974, :y 116, :z -18060})
(def anchor2 [-16974 116 -18055])
(def anchor3 [-16977 116 -18050])
(-> (chalet-section {:height 3 :depth 10 :width 3 :direction :east})
    (wc/set-blocks {:start anchor}))

(-> (chalet-section {:height 4 :depth 12 :width 3 :direction :south})
    (wc/set-blocks {:start anchor2}))
(-> (chalet-section {:height 6 :depth 7 :width 2 :direction :east})
    (wc/set-blocks {:start anchor3}))

(wc/fly! me)
(wc/set-fly-speed me 0.5)
(wc/xyz-round (wc/target-block me))
(wc/add-inventory me :diamond-pickaxe 1)
(wc/add-inventory me :cobbled-deepslate 64)
(wc/add-inventory me :cobbled-deepslate-slab 64)
(wc/fly-speed me)
(wc/undo!)

(wc/set-time 20000)
(wc/fly! me)
(wc/set-blocks
 (map #(conj (wc/xyz %) :stone)
      (fill/fill-xz (wc/target-block me) {:materials #{:snow :snow-block}
                                          :limit 5
                                          :throw? false}
                    )))
