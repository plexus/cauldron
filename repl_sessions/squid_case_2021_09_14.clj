(ns squid-case-2021-09-12
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.events :as e]
            [lambdaisland.witchcraft.palette :as p]
            [lambdaisland.witchcraft.matrix :as m]))

(def me (wc/player "sunnyplexus"))

(wc/add-inventory me :sand 64)

(wc/set-time 0)
(wc/fly!)

(defonce block (atom nil))

(defn neighbours
  [loc]
  (let [d [-1 0 1]]
    (for [dx d dy d dz d
          nloc [(wc/add (wc/location loc) [dx dy dz])]
          :when (not= (wc/location loc) nloc)
          block [(wc/get-block nloc)]
          :when (not (#{:air :spruce-planks} (wc/material-name block)))]
      block)))

(defn fill
  [start]
  (loop [search #{start}
         result #{start}]
    (let [new-blocks (reduce
                      (fn [res loc]
                        (into res (remove result) (neighbours loc)))
                      #{}
                      search)]
      (if (seq new-blocks)
        (recur new-blocks (into result new-blocks))
        result))))

(e/listen! :player-interact
           ::select-block
           (fn [{:keys [material player action] :as e}]
             (when (and (= player me)
                        (= action :left-click-block)
                        (= material (wc/material :wooden-axe)))
               (reset! block (:clickedBlock e))
               (let [block (:clickedBlock e)
                     [x y z] (wc/xyz block)
                     radius 1]
                 (run! #(.breakNaturally %)
                       (for [x (range (- x radius) (+ x radius))
                             y (range (- y radius) (+ y radius))
                             z (range (- z radius) (+ z radius))
                             :when (< (wc/distance block [x y z])
                                      radius)]
                         (wc/get-block {:x x :y y :z z}))
                       )))))

(future
  (println "start")
  (try
    (def clear-out-this (fill @block))
    (catch Exception e
      (println e)))
  (println "done"))

(wc/set-blocks
 (for [x (range 640 790)
       y (range 64 130)
       z (range -900 -720)]
   {:x x :y y :z z :material :air}))

(wc/undo!)

@block
{:x 767.0, :y 117.0, :z -697.0, :world "world", :material :basalt}
{:x 687.0,
 :y 64.0,
 :z -849.0,
 :world "world",
 :material :spruce-planks}
