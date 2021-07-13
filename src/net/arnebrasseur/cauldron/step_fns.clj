(ns net.arnebrasseur.cauldron.step-fns
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as c]))

(defn catenary-curve [x length]
  (let [y #(- (Math/cosh (/ % (/ length 2))) 1)
        x-factor (y (/ length 2))]
    (-  (/ (y x) x-factor) 1)))

(defn catenary [dip length]
  (let [delta  (fn [a b]
                 (Math/sqrt (* (Math/abs (- (:x a) (:x b)))
                               (Math/abs (- (:z a) (:z b))))))
        y-fn   #(* (catenary (-  % (/ length 2)) length) dip)
        ckey (keyword (gensym "catenary-start"))]
    (fn [curs dir]
      (let [[start curs] (if-let [start (get curs ckey)]
                           [start curs]
                           [(select-keys curs [:x :y :z])
                            (assoc curs ckey (select-keys curs [:x :y :z]))])
            [x _ z] (get c/movements dir)
            curs (-> curs (update :x + x) (update :z + z))
            dist (delta start curs)]
        (assoc curs :y (Math/round (+ (:y start) (y-fn dist))))))))

(def toppings #{:air :snow :chorus-flower :yellow-flower})

(defn follow-path
  "Try to stay on the same y-level as existing blocks, but only go up/down at most
  one block per step."
  [c dir]
  (let [c (c/step-fn c dir)]
    (cond
      (#{:air :snow :long-grass :double-plant :chorus-flower :yellow-flower}
       (wc/material-name (wc/get-block c)))
      (update c :y dec)
      (not (#{:air :snow :long-grass :double-plant :chorus-flower :yellow-flower}
            (wc/material-name (wc/get-block (update c :y inc)))))
      (update c :y inc))))

(defn clear-above
  "Remove any blocks directly above the block that was just set, really
  a :block-fn, not a step-fn"
  [c]
  (let [bv (c/block-value c)]
    (assoc c :blocks
           (loop [bv bv
                  blocks (conj (:blocks c) bv)]
             (let [bv (update bv :y inc)]
               (if (= :air (wc/material-name (wc/get-block bv)))
                 blocks
                 (recur bv (conj blocks (assoc bv :material :air :data 0)))))))))
