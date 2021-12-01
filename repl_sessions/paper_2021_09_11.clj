(ns paper-2021-09-11
  (:require [lambdaisland.classpath :as cp]
            [lambdaisland.witchcraft :as wc]))

(require 'lambdaisland.witchcraft.palette)

(cp/update-classpath! {})

(cp/classpath-chain)

(wc/set-time (wc/world "scratch") 0)

(defn me [] (wc/player "sunnyplexus"))

(wc/default-world)

(wc/fly! (me))

(doseq [sess (vals @@#'nrepl.middleware.session/sessions)]
  (alter-meta! sess assoc :witchcraft/whoami "sunnyplexus"))
(map (comp :witchcraft/whoami meta) (vals @@#'nrepl.middleware.session/sessions))
wc/*default-world*

(wc/loc {})

(wc/world (wc/player "sunnyplexus"))

(wc/create-world "scratch" {})

(wc/teleport (me) {:world "scratch"})

(wc/loc (me))

(wc/teleport (me) [0 64 0])
(wc/clear-weather)

(wc/fly! (me))

(wc/xyz (me))

(.getActiveItem (me))

(wc/set-health (wc/player "sunnyplexus") 20)
(wc/set-food-level (wc/player "sunnyplexus") 20)

(.setFlySpeed (me) 0.4)
(seq (.getActivePotionEffects (me)))
(wc/teleport (me) [-805 125 1203])
(wc/teleport (me) [291 125 45])

(wc/add-inventory (me) :diamond-pickaxe)
(wc/add-inventory (me) :diamond-axe)
(wc/add-inventory (me) :torch 64)

(.removePotionEffect (me) (.getType (first (.getActivePotionEffects (me)))))

(wc/teleport
 (me)
 {:world "scratch"})

(wc/clear-weather )

(wc/worlds)

(panic!)
(wc/location (me))
(wc/undo!)
(wc/location {:world "scratch"})

(let [l  {:world "world"}]
  (wc/teleport
   (me)
   (wc/map->Location
    (if (map? l)
      (merge (wc/loc (me))
             l)
      l)))
  )
(wc/undo!)

(defn panic! []
  (wc/set-blocks
   (let [[x y z] (map long (wc/xyz (me)))]
     (concat
      (for [x' (range (- x 2) (+ x 2))
            y' (range (- y 1) (+ y 3))
            z' (range (- z 2) (+ z 2))
            :when (or (#{(- x 2) (+ x 1)} x')
                      (#{(- y 1) (+ y 2)} y')
                      (#{(- z 2) (+ z 1)} z'))]
        {:x x'
         :y y'
         :z z'
         :material :obsidian})
      [{:x (dec x)
        :y (+ y 1)
        :z (dec z)
        :material :wall-torch
        :direction :east}]))))

(wc/undo!)
(afk-box!)
(.getSeed (wc/world  "world"))


(.createWorld (wc/server)
              (.seed (org.bukkit.WorldCreator. "scratch") -2649164462859857019))

(require '[clojure.reflect :as reflect])

(reflect/reflect (wc/server))

(panic!)

(wc/worlds)

(let [[x y z] (map long (wc/xyz (me)))]
  (doseq [x [(- x 2) (+ x 2)]
          y [(- y 1) (+ y 4)]
          z [(- z 2) (+ z 2)]]
    (wc/set-block {:x x
                   :y y
                   :z z
                   :material :obsidian})))

(wc/undo!)

(wc/set-block
 {:x 263
  :y 73
  :z 30
  :material :torch})

(wc/undo!)
