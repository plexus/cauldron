(ns advent-20211203
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.fill :as fill]
            [lambdaisland.witchcraft.markup :as markup]
            [lambdaisland.witchcraft.shapes :as shapes]
            [lambdaisland.witchcraft.events :as e]
            [lambdaisland.witchcraft.util :as util]))

(wc/online-players)

(wc/set-health (wc/player "sunnyplexus") 20)
(.setAdmin (wc/player "BombingCat2022") true)

(util/enum->map
 org.bukkit.potion.PotionEffectType)


(.addPotionEffect
 (wc/player "BombingCat2022")
 (org.bukkit.potion.PotionEffect.
  org.bukkit.potion.PotionEffectType/FAST_DIGGING 20000 5))
(.addPotionEffect
 (wc/player "sunnyplexus")
 (org.bukkit.potion.PotionEffect.
  org.bukkit.potion.PotionEffectType/SLOW_FALLING 20000 5))

(wc/fly! (wc/player "sunnyplexus"))

(wc/add-inventory (wc/player "BombingCat2022") :cooked-beef 64)

(wc/xyz (wc/player "sunnyplexus"))
[-17290.54680425053 67.0 -18043.293108803573]
(wc/xyz (wc/player "BombingCat2022"))
[222.2080559769712 71.70083236926058 141.5859591426322]

(wc/teleport (wc/player "sunnyplexus") [222 71 150])

(def decorator9000-name
  (wc/normalize-text
   (markup/fAnCy "Decorator 9000"
                 [:gold :gray :gold])))

(defn create-decorator []
  (let [axe (wc/item-stack :stone-axe)]
    (wc/set-display-name axe decorator9000-name)
    (wc/set-lore axe [[:italic "Twinkle, twinkle"]])
    axe))

(wc/add-inventory (wc/player) (create-decorator))

(e/listen!
 :player-interact
 ::decorator-9000
 (fn [{:keys [clickedBlock player action] :as evt}]
   (when (and clickedBlock
              (= decorator9000-name (wc/display-name (wc/item-in-hand player)))
              (= :spruce-log (wc/material-name clickedBlock)))
     (let [air-blocks
           (->> (fill/fill-xyz clickedBlock {:pred #(#{:spruce-log :spruce-leaves}
                                                     (wc/material-name %))})
                (filter #(= :spruce-leaves (wc/material-name %)))
                (map (fn [block]
                       (-> block
                           wc/location
                           (wc/add [0 -1 0])
                           wc/block)))
                (filter (fn [block]
                          (= :air (wc/material-name block))))
                shuffle)]
       (wc/set-block
        (assoc (first air-blocks) :material :lantern))))))


#_
(e/unlisten! :player-interact ::decorator-9000)

(wc/undo!)


(wc/xyz (wc/player "sunnyplexus"))
(def anchor [246 63 193])


(wc/set-blocks
 (map
  #(wc/add % anchor)
  (concat
   (shapes/box {:east-west-length 5
                :north-south-length 7
                :height 5
                :material :pink-wool
                :start [0 0 0]})
   (shapes/box {:east-west-length 2
                :north-south-length 3
                :height 5
                :material :pink-wool
                :start [1 4 -2]})
   )))

(wc/teleport (wc/player "sunnyplexus") anchor)

(wc/undo!)

(shap)
