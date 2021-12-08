(ns advent-20211202
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.markup :as markup]
            [lambdaisland.witchcraft.events :as e]))

(def me (wc/player "sunnyplexus"))
(wc/fly! me)

(def tree-loc {:x 1270.8106076128893, :y 76.0, :z 532.6999999880791, :pitch -25.2, :yaw -166.79993, :world "world"})
(def broken-portal-loc {:x 696.5994201197474, :y 67.25612220031708, :z -403.98962178411955, :pitch 10.999951, :yaw -148.34978, :world "world"})
(def jungle-bay-island {:x 1539.8106076128893, :y 146.0, :z -154.30000001192093, :pitch 54.30002, :yaw -174.14984, :world "world"})

(def dessert-village-loc
  {:x 16073.254390015913, :y 117.88111932175335, :z 13123.922814778323, :pitch 47.60001, :yaw -61.80014, :world "world"})
(def weird-peeks-loc
  {:x 21549.338576305265, :y 146.01237368850101, :z -21655.023155487757, :pitch 31.250086, :yaw -147.00081, :world "world"})
(def coral-loc {:x 2591.471500471456, :y 84.13112220002766, :z 1460.5103381126942, :pitch 36.29999, :yaw -91.95001, :world "world"})
(def mountain-spot
  {:x -16960.33844113195, :y 116.0, :z -18054.772657936446, :pitch 19.650095, :yaw 83.5524, :world "world"})

(wc/loc me)
(wc/xyz me)
(wc/teleport mountain-spot)


(wc/set-blocks
 (map #(wc/add % [-16967 117 -18053])
      (for [x (range -20 17)
            y [2 3 4]
            z (range -16 18)]
        [x y z :air])))

(wc/add-inventory  me :diamond-axe)

(wc/teleport me
             (wc/add tree-loc
                     [(- (rand-int 50000) 25000)
                      70
                      (- (rand-int 50000) 25000)]) )

(wc/loc me)

(defn normalize-text [txt]
  (wc/display-name
   (doto (wc/item-stack :wooden-axe 1)
     (wc/set-display-name txt))))

(defn fAnCy [string colors]
  (into [:<>]
        (map (fn [ch c]
               [c (str ch)])
             string (cycle colors))))

(def megachop-9000 (normalize-text
                    (markup/render
                     (fAnCy "MegaChop 9000"
                            [:yellow :gold :red]))))

(wc/display-name
 (wc/item-in-hand
  (wc/player)))



(let [axe (wc/item-in-hand (wc/player))]
  (wc/set-display-name axe (markup/render megachop-9000))
  (wc/set-lore axe
               ["Even the trees shivered"
                "when the Themjer passed."]))


(e/listen! :player-interact
           ::megachop-9000
           (fn [{:keys [clickedBlock player action]}]
             (when clickedBlock
               (let [material (wc/material-name clickedBlock)]
                 (when (and (= megachop-9000 (wc/display-name (wc/item-in-hand player)))
                            (re-find #"-log$" (name material)))
                   (run! (memfn breakNaturally)
                         (fill clickedBlock {:pred #(= material (wc/material-name %))
                                             :dy [-1 0 1]})))))))
