(ns xseries-adoption-2021-08-07
  (:require [lambdaisland.witchcraft.util :as util]
            [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as c]
            [net.arnebrasseur.cauldron :refer :all])
  (:import (com.cryptomorin.xseries XMaterial XBlock)
           org.bukkit.Bukkit
           org.bukkit.Material
           org.bukkit.inventory.ItemStack
           ))

(wc/start-glowstone!)

(.getHandle (wc/world "world"))

(.getVersion (wc/server))

(.getData (.get (XMaterial/matchXMaterial "RED_WOOL")) )

(util/enum->map XMaterial)

(Class/forName "com.cryptomorin.xseries.XMaterial")




(.setAmount (.parseItem (.get (XMaterial/matchXMaterial "RED_WOOL")) ) 10)

(wc/add-inventory (wc/player) :acacia-planks 1)

(wc/get-block [0 0 0])

(def me (wc/player))

(wc/loc me)
{:x -766.5, :y 71.0, :z 291.5, :pitch -7.350007, :yaw 358.80005, :world "world"}


(wc/in-front-of me 5)
#bukkit/Location [-766.1101024128617 89.36933190672691 298.48213110836485 1.1999817 -0.74999887 "world"]


(wc/set-block [-762.5 73.0 294.5 305.85 -21.000002 "world"] :dark-oak-stairs)

(wc/block [-762.5 73.0 294.5 305.85 -21.000002 "world"])
(wc/set-direction [-762.5 73.0 294.5 305.85 -21.000002 "world"] :north)

(wc/set-time 0)
(wc/fly! me)

(-> (c/start [-766 89 298] :south)
    (c/material :acacia-log)
    (c/steps 3)
    (c/rotate 2)
    (c/steps 3)
    (c/build!))

(wc/set-direction (wc/block [-766 89 298]) :south)

(wc/undo!)

(count @wc/undo-history)

(wc/set-direction [-764 72 293] :south)
(wc/set-time 0)

(capture-blocks!)

(last-block)
{:x -764, :y 72, :z 293, :material :magenta-glazed-terracotta, :data 0}
