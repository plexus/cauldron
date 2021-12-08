(ns advent-20211205
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.matrix :as m]))

;; getter
(wc/location (wc/target-block (wc/player "sunnyplexus")))
;; coerces
(wc/location (wc/location (wc/target-block (wc/player "sunnyplexus"))))
(wc/location (wc/vec3 0 1 2))
;; clojure -> java
(wc/location [0 0 0])
(wc/location {:x 0 :y 1 :z 1})
