(ns repl-sessions.citizens-2022-01-07
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.citizens :as c]
            [lambdaisland.witchcraft.events :as e]))

(wc/set-time 0)
(wc/set-game-rule (wc/world "world") :do-daylight-cycle false)
(wc/set-difficulty (wc/world "world") :peaceful)
(ancestors
 org.bukkit.craftbukkit.v1_17_R1.enchantments.CraftEnchantment)

(clojure.reflect/reflect org.bukkit.enchantments.Enchantment)

(def dragon (c/create-npc :ender-dragon "dee"))

(.remove (wc/entity dragon))
(.isSpawned dragon2)
(wc/spawn (wc/target-block me) gem)
(wc/spawn (wc/target-block me) dragon2)

(c/navigate-to dragon (wc/add (wc/location dragon) [0 -3 0]))

(c/update-traits dragon
                 {:follower {:follow me
                             :offset [0 10 0]
                             :max-dist 20
                             :min-dist 14}})

(def gem  (c/create-npc :player "GeminiTay"))

(c/traits jonny)

(def me (wc/player "sunnyplexus"))

(make-trait "fishing"
            {:on-attach (fn [this] (println "Fishing attached!"))
             :on-spawn (fn [this] (println "Fisher spawned!"))
             :run (fn [this])})

@(c/npc-trait jonny :follower)

(def inv (wc/inventory me))

(wc/set-inventory me
                  (assoc (wc/inventory me) 0 nil))
(.updateInventory me)
(wc/add-inventory me
                  {:material :golden-axe
                   :display-name "Goldiloxe"
                   :lore ["She pretty,"
                          "but she "
                          [:bold "Chop hard"]]
                   :enchants {:smite 3}})

(wc/item-stack
 {:material :golden-axe
  :display-name "Goldiloxe"
  :lore ["She pretty,"
         "but she " [:bold "Chop hard"]]})
(clojure.reflect/reflect me)
(wc/add-inventory me :diamond-axe)
(dotimes [i 1000]
  (wc/spawn (wc/add (wc/target-block me) [(/ (rand-int 300) 100)
                                          (/ (rand-int 300) 100)
                                          (/ (rand-int 300) 100)]) :experience-orb)
  )

(.setLevel me 30)
(.giveExpLevels me 1)
(wc/en)

(wc/set-time 0)

(wc/set-game-rule (wc/world "world")
                  :do-daylight-cycle
                  false)

(wc/spawn [0 0 0] jonny)

(.setTarget
 (navigator (npc-by-id 0))
 (wc/add (wc/location me) [-5 0 -5]))

(make-trait :test-trait2 {:init {:foo 123}})

(.openInventory me
                (wc/get-inventory jonny)
                #_(org.bukkit.Bukkit/createInventory nil 9 "Testing inventory"))

(wc/open-inventory me {:type :brewing
                       :title "xx"
                       :contents [{:material :lapis-block}
                                  {:material :blaze-rod}
                                  {:material :glass-bottle}]})
(wc/)
;; An example of the traits system, here I define a new trait, :follower, for an
;; NPC which follows another entity (e.g. a player). Once they are at least
;; `:min-dist` away from the entity they `:follow`, they will navigate towards
;; that entity, until they are at most `:max-dist` blocks away.

(c/make-trait
 ;; Name of the trait
 :follower
 ;; Map of callback and config
 {;; Initial value of the trait's state, for each instance. This state is kept
  ;; and automatically persisted.
  :init {:follow nil
         :min-dist 4
         :max-dist 2
         :following? false}
  ;; Run hook where we periodically check if we should navigate or not, we get
  ;; passed the trait instance, which we can deref to get the current state.
  :run
  (fn [this]
    (let [{:keys [min-dist max-dist follow following?]} @this]
      (when follow ; Kicks in as soon as we are following someone
        (if following?
          (if (< max-dist (wc/distance (c/npc this) follow))
            (c/navigate-to this follow)
            (c/stop-navigating this))
          (when (< min-dist (wc/distance (c/npc this) follow))
            (c/navigate-to this follow))))))})

(c/update-traits
 jonny
 {:follower {:follow me
             :min-dist 4
             :max-dist 1}})

(c/remove-trait jonny :follower)

(e/listen! :citizens/npcright-click
           ::carrier
           (fn [e]
             (when (c/has-trait? (:NPC e) :carrier)
               (wc/open-inventory
                (:clicker e)
                (:inventory @(c/npc-trait (:NPC e) :carrier))))))

(c/make-trait :carrier
              {:init {:inventory (wc/make-inventory {:capacity 18})}})

(c/npc-trait
 gem
 :carrier)
