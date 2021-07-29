(ns block-set
  (:require [lambdaisland.witchcraft :as wc]))

(def s (atom (wc/block-set nil)))


(swap! s conj {:x 10 :y 10 :z 10 :material :stone})

(proxy [clojure.lang.PersistentTreeSet]
    [nil (clojure.lang.PersistentTreeMap. nil (comparator
                                               #(compare ((juxt :x :z :y) %1)
                                                         ((juxt :x :z :y) %2))
                                               ))])
