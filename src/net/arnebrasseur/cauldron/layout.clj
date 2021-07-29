(ns net.arnebrasseur.cauldron.layout
  "Functions for dealing with layouts, data structures describing the shape of
  things. The main structures we deal with are

  - point {:x :y :z}
  - corners: sequence of point, forming lines in the order they are given
  - line segments {:a point :b point :length int :dir keyword}
  - path [sequence of line segments]
  - outline: collection of points that form a contiguous closed shape
  - plane: outline + all points that lie on the inside of the shape

  A typical path would the floor plan of a house
  "
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as c]))

(defn signed-one
  "Reduce the number to 0/1/-1 based on its sign"
  [n]
  (cond
    (= 0 n)
    0
    (< 0 n)
    1
    (< n 0)
    -1))

(defn direction-v
  [from to]
  (mapv #(signed-one (- (get to %) (get from %))) [:x :y :z]))

(defn direction-kw
  "Given two points, return a cardinal direction (keyword)
   that gets you from point a to point b."
  [from to]
  (let [dv (direction-v from to)]
    (get (into {} (map (juxt val key)) c/movements) dv)))

(defn line-segment
  "Given two points (x/y/z), return a line-segment, a map containing :a/:b (the
  two points), the length, and the direction."
  [a b]
  {:a (select-keys a [:x :y :z])
   :b (select-keys b [:x :y :z])
   :length (let [d (wc/distance a b)]
             (if (= d (Math/abs d))
               (long d)
               d))
   :dir (direction-kw a b)})

(defn corners->path
  "Turn a set of points into a sequence of line segments"
  [ps]
  (vec
   (for [[from to] (partition 2 1 (concat ps [(first ps)]))]
     (line-segment from to))))

(defn sketch-path!
  "Make a path visible in magenta-glazed terracotta"
  [path]
  (as-> (c/start) $
    (reduce (fn [c {:keys [a dir length]}]
              (-> (merge c a)
                  (c/block)
                  (c/steps length dir)))
            $
            path)
    (c/build! $)))

(defn path->outline
  "Given a path, get a collection of blocks that walk the path of the path"
  [path]
  (->> path
       (reduce
        (fn [c {:keys [a dir length]}]
          (-> c
              (merge a)
              (c/block)
              (c/steps length dir)))
        (c/start))
       :blocks
       (map #(select-keys %[:x :y :z]))))

(defn neighbour-locs
  "Get all neighbouring locations in the x/z plane, with an extra args (predicate
  or set) limit the result"
  ([pred loc]
   (wc/block-set
    (filter pred)
    (neighbour-locs loc)))
  ([{:keys [x y z] :as loc}]
   (wc/block-set
    (for [x [(dec x) x (inc x)]
          z [(dec z) z (inc z)]
          :let [point {:x x :y y :z z}]
          :when (not= loc point)]
      point))))

(defn outline->inner-plane
  "Given a set of blocks forming a closed shaped, return the blocks that form the
  inside of the shape."
  [outline]
  (let [outline (wc/block-set outline)
        ;; Pick a random block from the outline
        seed (first outline)
        ;; Find two spots symmetrically on two sides of the seed block that are
        ;; not part of the outline. We can assume that one of them is on the
        ;; inside, and one on the outside.
        [[seeda seedb]] (for [[dx dz] [[1 0] [0 1] [1 1]]
                              :let [sa (-> seed
                                           (update :x + dx)
                                           (update :z + dz))
                                    sb (-> seed
                                           (update :x - dx)
                                           (update :z - dz))]
                              :when (not (some #{sa sb} outline))]
                          [sa sb])
        grow (fn [search result]
               (reduce
                (fn [res loc]
                  (into res
                        (comp (remove result)
                              (remove outline))
                        (neighbour-locs loc)))
                #{}
                search))
        ;; Grow from both seeds, until one terminates
        inner (loop [searcha (wc/block-set [seeda])
                     resulta (wc/block-set [seeda])
                     searchb (wc/block-set [seedb])
                     resultb (wc/block-set [seedb])]
                (let [new-blocksa (grow searcha resulta)
                      new-blocksb (grow searchb resultb)]
                  ;; This is very satisfying to watch
                  #_(do
                      (wc/set-blocks
                       (concat
                        (map #(assoc % :material :red-glazed-terracotta) new-blocksa)
                        (map #(assoc % :material :blue-glazed-terracotta) new-blocksb)))
                      (Thread/sleep 1000))
                  (cond
                    (empty? new-blocksa)
                    resulta
                    (empty? new-blocksb)
                    resultb
                    :else
                    (recur new-blocksa (into resulta new-blocksa)
                           new-blocksb (into resultb new-blocksb)))))
        remain (remove (wc/block-set (mapcat neighbour-locs inner)) outline)]
    (if (seq remain)
      (into inner (outline->inner-plane remain))
      inner)))

(defn plane->outline
  "given a contiguous set of locs, get a collection of the outermost ones"
  [locs]
  (let [locs (wc/block-set locs)]
    (filter (fn [loc]
              (seq (remove locs (neighbour-locs loc))))
            locs)))

(defn shrink-plane [plane]
  (-> plane
      plane->outline
      outline->inner-plane))

(defn shrink-outline [outline]
  (-> outline
      outline->inner-plane
      plane->outline))

(defn grow-plane [plane]
  (wc/block-set
   (mapcat neighbour-locs)
   plane))

(defn outline->plane [outline]
  (into outline (outline->inner-plane outline)))

(defn plane-corner? [plane loc]
  (when (#{3 7} (count (neighbour-locs plane loc)))
    loc))

(defn plane->corner-set
  "Corners of the plane, but not sorted"
  [plane]
  (let [plane (wc/block-set plane)]
    (wc/block-set
     (filter #(plane-corner? plane %)
             plane))))

(defn outline->path [outline]
  (-> outline
      outline->plane
      plane->corner-set
      corners->path))

(defn path->plane [path]
  (outline->plane (path->outline path)))

;; get the corners
;; pick one
;; walk the edge of the plane in some direction until next corner
;; repeat until we're done/back at the start

(defn plane->path [plane]
  (let [plane (wc/block-set plane)
        corners (plane->corner-set plane)]
    (loop [start (first corners)
           loc start
           seen (wc/block-set [loc])
           remaining (disj corners start)
           path []]
      (let [next (wc/add loc (direction-v start loc))
            candidates
            (if (and (contains? plane next) (not (contains? seen next)))
              [next]
              (neighbour-locs
               (fn [n]
                 (and
                  ;; only consider candidates that are part of the plane
                  (contains? plane n)
                  ;; skip the ones we've dealt with already
                  (not (contains? seen n))
                  ;; skip inner points
                  (not= 8 (count (neighbour-locs plane n)))
                  ;; skip diagonals
                  (< (+ (Math/abs (- (:x loc) (:x n)))
                        (Math/abs (- (:z loc) (:z n)))) 2)))
               loc))
            corner (some remaining candidates)
            next (or corner (first candidates))]
        #_(when next
            (wc/set-block (assoc next :material :green-glazed-terracotta))
            (Thread/sleep 500))
        (cond
          (nil? next)
          (let [end (first (neighbour-locs (wc/block-set (map :a path)) loc))]
            (prn loc
                 (neighbour-locs loc)
                 (wc/block-set (map :a path)))
            (if (empty? remaining)
              (conj path (line-segment start end))
              (let [new-start (first remaining)]
                (recur new-start
                       new-start
                       (conj seen new-start)
                       (disj remaining new-start)
                       (conj path (line-segment start end))))))
          corner
          (recur corner
                 corner
                 (conj seen corner)
                 (disj remaining corner)
                 (conj path (line-segment start corner)))
          :else
          (recur start
                 next
                 (conj seen next)
                 remaining
                 path))))))

(defn shrink-path [path]
  (-> path
      path->outline
      outline->inner-plane
      plane->path))

(defn grow-path [path]
  (-> path
      path->plane
      grow-plane
      plane->path))
