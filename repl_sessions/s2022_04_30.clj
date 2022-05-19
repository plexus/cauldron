(ns repl-sessions.s2022-04-30
  (:require [lambdaisland.witchcraft :as wc]))

(wc/fly!(wc/player))

(wc/set-fly-speed (wc/player)0.3)
(wc/fly-speed (wc/player) )

(wc/allow-flight (wc/player) false)

(wc/set-time 0)

(wc/add-inventory )

(wc/into-inventory (wc/get-target-block (wc/player))
                   [
                    [:chest 64]                    ])

(def lchest (wc/get-target-block (wc/player)))

(.setExperience (wc/spawn (wc/add (wc/location (wc/player)) [0 3 0]) :experience-orb) 10000)

(wc/block )

(supers (class (.getState (wc/get-target-block (wc/player)))))


(wc/inventory lchest)

(wc/remove-inventory lchest {:material :azure-bluet, :amount 1, :display-name "bloemetje"})

(def bluet (first (wc/get-inventory lchest)))
bluet
(wc/remove-inventory lchest bluet)

(wc/add-inventory rchest bluet)

bluet

(wc/remove-inventory rchest
                     (wc/item-stack {:material :azure-bluet, :amount 1, :display-name "bloemetje"}))

(.isSimilar
 (first (wc/get-inventory (wc/player)))
 (wc/item-stack {:material :azure-bluet, :amount 1, :display-name "bloemetje"}))

(apply =)
(for [item [bluet
            (wc/item-stack {:material :azure-bluet, :amount 1, :display-name "bloemetje"})]]
  [(.getType item)
   (.getDurability item)
   (.getItemMeta item)])

(= bluet
   (let [is (.parseItem (wc/xmaterial :azure-bluet))]
     (wc/-set-item-meta
      is
      (doto (wc/item-meta is)
        (.displayName (net.kyori.adventure.text.Component/text "bloemetje"))
        ))
     is
     ))

(lambdaisland.witchcraft.markup/render "bloemetje")
[
 (.displayName(wc/item-meta bluet))
 (.displayName(wc/item-meta
               (wc/item-stack {:material :azure-bluet, :amount 1, :display-name "bloemetje"})))]

(clojure.reflect/reflect net.kyori.adventure.text.TextComponentImpl)

(clojure.reflect/reflect (wc/item-meta bluet))

(instance? net.kyori.adventure.text.Component (net.kyori.adventure.text.Component/text "bloemetje"))



(let [bluet (first (wc/get-inventory (wc/player)))]
  (wc/set-item-meta
   bluet
   (let [im (wc/item-meta bluet)]
     (.displayName
      im
      (lambdaisland.witchcraft.adventure.text/component
       [:blue
        "Hello"
        [:underlined " under"
         [:bold " bold"
          [:green " green"]]]


        ])
      )
     im)))
(.displayName )


(lambdaisland.witchcraft/init-xmaterial!)
(alter-var-root (var lambdaisland.witchcraft/server-type) (constantly :paper))

(wc/add-inventory (wc/player) :music-disc-otherside)

(wc/inventory (first (wc/get-inventory (wc/player))))
