(ns net.arnebrasseur.cauldron.curves
  (:require [lambdaisland.data-printers]
            [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.matrix :as m]
            [lambdaisland.witchcraft.shapes :as shapes])
  (:import (com.graphbuilder.curve BSpline
                                   Curve
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

(wc/defprint MultiPath 'curvesapi/MultiPath
  (fn [^MultiPath mp]
    {:dimension (.getDimension mp)
     :numPoints (.getNumPoints mp)
     :flatness (.getFlatness mp)}))

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

(defn bspline* ^BSpline [points {:keys [degree control-string]
                                 :or {degree 3
                                      control-string "0:n-1"}}]
  (let [cp (control-path points)
        gi (group-iterator control-string cp)]
    (doto (BSpline. cp gi)
      (.setDegree (min degree (dec (count points)))))))

(defn multipath ^MultiPath [dimension & curves]
  (let [mp (MultiPath. dimension)]
    (run! #(.appendTo ^Curve % mp) curves)
    mp))

(defn mp-seq [^MultiPath mp]
  (for [idx (range (.getNumPoints mp))]
    (vec (.get mp idx))))

(defn bspline-nodes
  ([points & [opts]]
   (mp-seq (multipath 2 (bspline* points opts)))))

(defn connect-segments [nodes & [opts]]
  (->> nodes
       (partition 2 1)
       (mapcat
        (fn [[[ax az] [bx bz]]]
          (shapes/line
           {:start [(long ax) (:y opts 0) (long az)]
            :end  [(long bx) (:y opts 0) (long bz)]
            :material (:material opts)})))))

(defn bspline
  ([points & [opts]]
   (connect-segments (bspline-nodes points opts) opts)))

(defn parallel-nodes
  "Construct a parallel curve by using the cross product of each segment to offset
  each node."
  [distance nodes]
  (for [[[x1 z1] [x2 z2]] (partition 2 1 nodes)
        :when (not= [x1 z1] [x2 z2])]
    (let [[x _ z] (m/v+ [x1 0 z1]
                        (m/cross-product (m/vnorm (m/v- [x2 0 z2] [x1 0 z1]))
                                         [0 distance 0]))]
      [x z])))
