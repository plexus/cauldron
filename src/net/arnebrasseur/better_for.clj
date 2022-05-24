(ns net.arnebrasseur.better-for)

(defmacro for*
  "Like regular for but,
  - get indices with `:idx`
  - finish up with an `:into`
  - allow multiple forms in the body (presumably for side effects)"
  [seq-exprs & body-expr]
  (let [pairs (partition 2 seq-exprs)
        last-pair (last pairs)
        idxs (filter (comp #{:idx} first) pairs)
        idx-syms (map (comp gensym str second) idxs)
        form `(clojure.core/for
                  ~(first
                    (reduce
                     (fn [[acc syms] [k v]]
                       (case k
                         :idx [(conj acc :let `[~v (vswap! ~(first syms) inc)]) (next syms)]
                         :into [acc syms]
                         [(conj acc k v) syms]))
                     [[] idx-syms]
                     pairs))
                (do ~@body-expr))
        form (if (seq idxs)
               `(let ~(into [] (mapcat (fn [s] [s `(volatile! -1)]))
                            idx-syms)
                  ~form)
               form)]
    (if (= :into (first last-pair))
      `(into ~(second last-pair) ~form)
      form)))
