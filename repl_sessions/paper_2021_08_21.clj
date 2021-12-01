(ns paper-2021-08-21
  (:require [lambdaisland.witchcraft :as wc]))

(def me (wc/player "sunnyplexus"))

(wc/run-task
 #(wc/set-time 0))

(wc/add-inventory me :elytra)
(wc/add-inventory me :firework-rocket 64)

wc/materials


(wc/init-xmaterial!)
