(ns witchcraft-server
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as c]
            [net.arnebrasseur.cauldron :refer :all]))

(defn me [] (wc/player "sunnyplexus"))

(def mini-base-loc {:x -12, :y 99, :z -29})

(wc/set-blocks (map #(assoc % :material :air) (map wc/block (fill  {:x -12, :y 99, :z -29}))))

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
      (roof (+ width overhang overhang) length)
      ))

(-> (c/start {:x -20, :y 98, :z -31} :east)
    (vanilla-house {:width 6
                    :length 5
                    :height 3
                    :overhang 1
                    :wall :wood
                    :corner :stone
                    :roof :wood-stairs
                    :roof-sides :brick})
    (c/build!))
;; => {:x 103, :y 90, :z -3, :material :air, :data 0}


(wc/fly! (me))
(wc/undo!)
(capture-blocks!)
(wc/clear-weather)
(wc/add-inventory (me)  1)
(wc/set-time 0)

(wc/teleport (me) (.setSpawnLocation (wc/world "world")))

(map :y (last-blocks 4))

(wc/set-blocks
 (for [x (range -20 -17)
       y [98]
       z (range -30 -26)]
   {:x x :y y :z z :material :wood :data 4}))

(defn online-players []
  (map #(.getName %)
       (.getOnlinePlayers (wc/server))))

(wc/location (wc/player "sunnyplexus"))

(online-players)

(wc/block (last-block))

(ns witchcraft-server
  (:gen-class)
  (:require [lambdaisland.witchcraft :as wc]
            [nrepl.cmdline :as nrepl]))

(defn -main [& args]
  (wc/start!)
  (nrepl/-main ["--middleware" "[refactor-nrepl.middleware/wrap-refactor,cider.nrepl/cider-middleware]"
                "-p" "32120"]))
