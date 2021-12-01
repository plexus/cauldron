(ns repl-sessions.halloween-2021
  (:require [lambdaisland.witchcraft :as wc]
            [net.arnebrasseur.cauldron :as c]
            [lambdaisland.witchcraft.palette :as pal]
            [net.arnebrasseur.cauldron.layout :as lay]))

(defn range* [a b]
  (if (< a b)
    (range a (inc b))
    (range b (inc a))))

(defn rect [[x1 z1] [x2 z2] y]
  (for [x (range* x1 x2)
        y [y]
        z (range* z1 z2)
        :when (or (#{x1 x2} x)
                  (#{z1 z2} z))]
    [x y z]))

(defn rect-fill [[x1 z1] [x2 z2] y]
  (for [x (range* x1 x2)
        y [y]
        z (range* z1 z2)
        :when (not (or (#{x1 x2} x)
                       (#{z1 z2} z)))]
    [x y z]))

(defn four-corners [[[x1 z1] [x2 z2]] y]
  [[x1 y z1]
   [x1 y z2]
   [x2 y z1]
   [x2 y z2]])

(defn merge-rooms [rooms y]
  (let [corners (mapcat #(four-corners % y) rooms)
        rects (map #(rect (first %) (second %) y) rooms)]
    (concat corners)
    (for [rect rects
          block rect
          :let [n (map (comp #(mapv long %) wc/xyz) (c/neighbours block {:pred (constantly true)}))
                common (filter (set n)
                               (mapcat identity (remove #{rect} rects)))]
          :when (or (and (some #{block} corners)
                         (< (count common) 4))
                    (< (count common) 3))]
      block)))

(def rooms
  {:red {:a [440 -968] :b [452 -976] :type :rect}
   :blue {:a [453 -965] :b [463 -976] :type :rect}
   :green {:a [444 -977] :b [462 -987] :type :rect}
   :yellow {:a [443 -967] :b [452 -955] :type :rect}
   })

(def foundation
  {:stone-bricks 30
   :cracked-stone-bricks 10
   :infested-mossy-stone-bricks 5
   :infested-cracked-stone-bricks 2})


(def levels
  {#{:red :blue :green :yellow}
   {(range* 67 72) {:outer foundation}
    73 {:outer {:iron-bars 3
                :cobblestone-wall 1}
        :fill :jungle-planks}
    74 {:outer :polished-andesite
        :fill :oak-planks}
    75 :air
    76 :air
    77 :air}})

(defn room-blocks [m y area]
  (case [(:type m) area]
    [:rect :outer] (rect (:a m) (:b m) y)
    [:rect :fill] (rect-fill (:a m) (:b m) y)
    ))

(comment

  (wc/set-blocks
   (for [[room-keys levels] levels
         [y spec] levels
         y (if (number? y) [y] y)
         [area material] (if (keyword? spec) {:outer spec} spec)
         [k room-spec] (filter (comp room-keys key) rooms)
         block (room-blocks room-spec y area)
         :let [material (if (map? material)
                          (pal/rand-palette material)
                          material)]]
     (conj block material)))

  (wc/set-blocks
   (for [[k [a b]] rooms
         block (rect a b 75)]
     (conj block (keyword (str (name k) "-concrete")))))

  (wc/set-blocks (map #(conj % :purple-wool) (merge-rooms (vals rooms) 75)))
  (wc/set-blocks (map #(conj % :blue-wool) (mapcat #(four-corners % 75) (vals rooms))))
  (wc/undo!)

  (lay/outline->path (map wc/loc (merge-rooms (vals rooms) 75)))


  (wc/fly! (wc/player))

  (wc/add-inventory  (wc/player) :dark-oak-door 2)

  (keys wc/materials))
