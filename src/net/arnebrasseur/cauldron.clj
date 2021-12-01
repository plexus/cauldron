(ns net.arnebrasseur.cauldron
  (:require [lambdaisland.witchcraft :as wc]))

(defn neighbours
  "Find all neighbours of a block along the given axes, defaults to :x/:z, i.e.
  neighbours in a horizontal plane."
  ([loc]
   (neighbours loc nil))
  ([loc {:keys [dx dy dz pred]
         :or {dx [-1 0 1]
              dy [0]
              dz [-1 0 1]
              pred #(not= (wc/material-name %) :air)}}]
   (for [dx dx dy dy dz dz
         nloc [(wc/add (wc/location loc) [dx dy dz])]
         :when (not= (wc/location loc) nloc)
         block [(wc/get-block nloc)]
         :when (pred block)]
     block)))

(defn fill
  "Recursively find neighbours"
  ([start]
   (fill start nil))
  ([start opts]
   (loop [search #{start}
          result #{start}]
     (let [new-blocks (reduce
                       (fn [res loc]
                         (into res (remove result) (neighbours loc opts)))
                       #{}
                       search)]
       (if (seq new-blocks)
         (recur new-blocks (into result new-blocks))
         result)))))

(defn me [] (wc/player "sunnyplexus"))

(defn whereami []
  (let [me (me)]
    [(wc/x me) (wc/y me) (wc/z me)]))

(defn find-land
  "Pick a random location on the map, and if it seems like there is land there,
  then teleport over."
  []
  (let [loc [(rand-int 2000) 70 (rand-int 2000)]]
    (when (not= :air (:material (wc/block loc)))
      (prn loc)
      (loop [loc loc]
        (if (and (= :air (:material (wc/block loc)))
                 (< (second loc) 108))
          (do
            (prn loc)
            (wc/teleport (assoc loc 1 105)))
          (recur (update loc 1 inc)))))))

(defonce block-history (atom ()))

(defn capture-blocks! []
  (wc/listen! :player-interact ::capture-block
              (fn [e]
                (when (:clickedBlock e)
                  (swap! block-history conj (:clickedBlock e))))))

(defn last-block []
  (wc/block (first @block-history)))

(defn last-blocks [n]
  (map wc/block (take n @block-history)))

(defn clear-surface!
  "Remove all neighbouring blocks, searching all cardinal directions plus upward"
  [loc]
  (wc/set-blocks
   (for [block (fill loc {:dy [0 1]})]
     {:x (wc/x block)
      :y (wc/y block)
      :z (wc/z block)
      :material :air})))

(defn locv [loc]
  [(wc/x loc) (wc/y loc) (wc/z loc)])

(defn locm [loc]
  {:x (wc/x loc)
   :y (wc/y loc)
   :z (wc/z loc)
   :material (wc/material-name loc)
   :data (wc/material-data loc)})
