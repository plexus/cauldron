(ns cauldron-2021-08-02
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as c]
            [net.arnebrasseur.cauldron :refer :all]
            [net.arnebrasseur.cauldron.layout :refer :all]
            [net.arnebrasseur.cauldron.step-fns :as steps]
            [net.arnebrasseur.cauldron.roll-tables :as rolls]
            [net.arnebrasseur.cauldron.structures :as struct]))

(wc/start!)

(wc/fly!)

;; Keep it day
(future
  (while true
    (Thread/sleep 5000)
    (when (< 12000 (wc/time))
      (wc/set-time 0))))


(defn flood [seed]
  (let [block-pred #(and (#{:grass :dirt} (:material (wc/block %)))
                         (#{:air :long-grass :snow} (:material (wc/block (update % :y inc)))))]
    (loop [search #{seed}
           result #{seed}]
      (let [new-blocks (reduce
                        (fn [res loc]
                          (into res (remove result)
                                (neighbour-locs block-pred loc)))
                        #{}
                        search)]
        (if (seq new-blocks)
          (recur new-blocks (into result new-blocks))
          result)))))

(def base-plane
  (flood {:x 2373, :y 78, :z 307}))

(defn bounding-box [locs]
  [(apply min (map :x locs))
   (apply min (map :z locs))
   (apply max (map :x locs))
   (apply max (map :z locs))])

(wc/set-blocks
 (let [[x1 z1 x2 z2] (bounding-box base-plane)
       y (:y (first base-plane))]
   (for [x (range x1 (inc x2))
         :let [line (filter (comp #{x} :x) base-plane)]
         z (range (apply min (map :z line))
                  (apply max (map :z line)))]
     {:x x :z z :y y :material :grass})))

(defn roof-row [size c]
  (-> c
      (c/excursion #(c/steps % size))
      (c/rotate 2)
      (c/move 1 :forward 1 :up)
      (c/rotate -2)))

(defn roof [c width length]
  (let [half-width (long (Math/ceil (/ width 2)))]
    (-> c
        (c/excursion #(c/reps % half-width (partial roof-row length)))
        (c/move (inc length))
        (c/rotate 2)
        (c/move (dec width))
        (c/rotate 2)
        (c/reps half-width (partial roof-row length)))))

(def house-def
  {:width 5
   :length 5
   :height 3})

(defn vanilla-house [c
                     {:keys [width length height overhang]
                      :as palette}]
  (-> c
      (c/palette palette)
      (c/reps height
              (fn [c]
                (-> c
                    (c/move 1 :up)
                    (c/reps 2 #(-> %
                                   (c/material :wall)
                                   (c/steps (- length 2))
                                   (c/material :corner)
                                   (c/step)
                                   (c/rotate 2)

                                   (c/material :wall)
                                   (c/steps (- width 2))
                                   (c/material :corner)
                                   (c/step)
                                   (c/rotate 2))))))
      (c/move 1 :up
              1 :backward
              overhang :left
              overhang :down)

      (c/material :roof)
      (roof (+ width overhang overhang) length)))

(-> (c/start (last-block) :north)
    (vanilla-house {:width 10
                    :length 9
                    :heights 7
                    :overhang 3
                    :wall :wood
                    :corner :stone
                    :roof :wood-stairs})
    (c/build!))

(wc/set-blocks
 (map #(assoc % :material :purple-glazed-terracotta) base-plane))

(wc/clear-weather)
