(ns repl-sessions.s2022-05-19
  "First experiments with the curves API"
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.data-printers]
            [lambdaisland.witchcraft.shapes :as shapes])
  (:import (com.graphbuilder.curve BSpline
                                   ControlPath
                                   GroupIterator
                                   Point
                                   MultiPath)
           (com.graphbuilder.geom PointFactory PointFactory$Point2D)))

(set! *warn-on-reflection* true)

(wc/defprint PointFactory$Point2D 'curvesapi/Point2D
  (fn [^PointFactory$Point2D p] [(.getX p) (.getY p)]))

(wc/defprint ControlPath 'curvesapi/ControlPath
  (fn [^ControlPath cp]
    {:numPoints (.numPoints cp)
     :numCurves (.numCurves cp)}))

(wc/defprint GroupIterator 'curvesapi/GroupIterator
  (fn [^GroupIterator gi]
    {:controlString (.getControlString gi)
     :groupSize (.getGroupSize gi)}))

(wc/defprint BSpline 'curvesapi/BSpline
  (fn [^BSpline bs]
    {:degree (.getDegree bs)
     :controlPath (.getControlPath bs)}))

(def me (wc/player "sunnyplexus"))

;; (wc/add-inventory me :elytra)

(wc/xyz-round (wc/target-block me))

(def p-1 [944 95 476])
(def p-2 [948 101 518])
(def p-3 [885 102 491])

;; (wc/set-blocks (map #(conj % :pink-wool) [p-1 p-2 p-3]))

;; (wc/fly! me)
;; (wc/teleport me {:x 911.9598365152642, :y 98.0, :z 494.58665768368206, :pitch 10.65000057220459, :yaw -88.32994842529297, :world "world"})

(defn point ^Point [o]
  (PointFactory/create (wc/x o) (wc/z o)))

(defn control-path ^ControlPath [points]
  (let [cp (ControlPath.)]
    (run! #(.addPoint cp (point %)) points)
    cp))

(defn group-iterator
  ^GroupIterator
  ([control-path]
   (group-iterator "0:n-1" control-path))
  ([control-string ^ControlPath control-path]
   (GroupIterator. control-string (.numPoints control-path))))

(defn bspline ^BSpline [points {:keys [degree control-string]
                                :or {degree 3
                                     control-string "0:n-1"}}]
  (let [cp (control-path points)
        gi (group-iterator control-string cp)]
    (doto (BSpline. cp gi)
      (.setDegree (min degree (dec (count points)))))))

(defn draw-bspline [points opts]
  (let [mp (MultiPath. 2)]
    (.appendTo (bspline points opts) mp)
    (->> (range (.getNumPoints mp))
         (map #(.get mp %))
         (partition 2 1)
         (mapcat (fn [[^"[D" from ^"[D" to]]
                   #_[[(aget from 0) (:z opts 0) (aget from 1) (:material opts)]]
                   (shapes/line {:start [(long (aget from 0)) (:z opts 0) (long (aget from 1))]
                                 :end  [(long (aget to 0)) (:z opts 0) (long (aget to 1))]
                                 :material (:material opts)}))))))

(wc/set-blocks
 (draw-bspline
  [[865 103 496]
   [891 92 450]
   [940 90 500]

   [960 76 429]
   [997 91 454]
   [988 86 497]
   [950 0 490]
   [970 96 536]]
  {:material :bone-block
   :z 105})
 {:anchor
  [-7 0 9]})

(wc/set-blocks
 (let [spline (draw-bspline
               [[865 103 496]
                [891 92 450]
                [940 90 500]

                [960 76 429]
                [997 91 454]
                [988 86 497]
                [950 0 490]
                [970 96 536]] nil)]
   (for [[x _ z] spline
         y (range 70 140)]
     [x y z (rand-nth [:mossy-cobblestone :stone-bricks :cobblestone
                       :orange-stained-glass])]))
 {:anchor
  [-7 0 9]})

(wc/undo!)
