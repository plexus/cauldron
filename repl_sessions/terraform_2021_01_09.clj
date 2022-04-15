(ns repl-sessions.terraform-2021-01-09
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.events :as e]
            [lambdaisland.witchcraft.fill :as fill]
            [lambdaisland.witchcraft.gallery.birchwood-lodge :as lodge]))

(def blk)

(e/listen! :player-interact ::blk
           (fn [e]
             (def blk (:clickedBlock e))))

(wc/set-blocks
 (map #(dissoc (assoc (wc/block %) :material :air) :block-data)
      (fill/fill-xz blk {:limit 5 :throw? false :materials #{:dirt :grass-block :dirt-path}})))
e/events
(wc/xyz blk)
[-231.0 117.0 -442.0]

(lodge/)
(wc/undo!)
(let [anchor [-140 69 -500]]
  (-> (lodge/lodge-section {:height 10 :depth 16 :width 4 :direction :east})
      (wc/set-blocks {:start (wc/add anchor [5 0 6])}))
  (-> (lodge/lodge-section {:height 10 :depth 10 :width 4 :direction :east})
      (wc/set-blocks {:start (wc/add anchor [-14 0 6])}))
  (-> (lodge/lodge-section {:height 17 :depth 13 :width 3 :direction :south})
      (wc/set-blocks {:start anchor}))
  #_
  (wc/set-blocks
   (map #(assoc (dissoc (wc/block %) :block-data) :material :oak-planks)
        (concat
         (fill/fill-xz (wc/add anchor [6 0 7])
                       {:pred #(not= :stripped-birch-wood (wc/mat %))
                        })
         (fill/fill-xz (wc/add anchor [-13 0 6])
                       {:pred #(not= :stripped-birch-wood (wc/mat %))
                        })
         (fill/fill-xz (wc/add anchor [1 0 0])
                       {:pred #(not= :stripped-birch-wood (wc/mat %))
                        }))))

  (wc/set-blocks (lodge/lantern-bar-gen {:chain-width 5
                                         :bar-width 9
                                         :lanterns 3
                                         :chain-length 5
                                         :axis :x})
                 {:start
                  (wc/add [12 12 6]
                          anchor)}

                 )
  (wc/set-blocks (lodge/lantern-bar-gen {:chain-width 5
                                         :bar-width 9
                                         :lanterns 3
                                         :chain-length 5
                                         :axis :x})
                 {:start
                  (wc/add [12 12 6]
                          anchor)}

                 )
  )

(wc/undo!)
