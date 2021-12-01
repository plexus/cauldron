(ns paper-2021-09-12
  (:require [lambdaisland.witchcraft :as wc]))

(def me (wc/player "sunnyplexus"))
(wc/fly! me)
(wc/set-time 0)
(wc/xyz me)

(def pos (atom {:x 262 :y 110 :z 51
                :pitch 73
                :yaw 6}))

(wc/yaw me)
(wc/teleport me [183 81 985]) ;; spawn
(wc/teleport me [750 100 1525])
(wc/teleport me [650 100 -850])
(future
  (doseq [x (range -3000 3000 100)
          z (range -3000 3000 100)
          :when (not (and (< -2000 x 2000)
                          (< -2000 z 2000)))]
    (let [pos (assoc @pos :x x :z z)]
      (wc/run-task
       #(wc/teleport me pos)))
    (Thread/sleep 500)))


(let [[x y z] (wc/xyz me)]
  (.setSpawnLocation (wc/world "world") x y z))

(wc/add-inventory me :birch-sign)
(wc/add-inventory me :glow-ink-sac)
