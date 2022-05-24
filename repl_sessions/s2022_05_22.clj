(ns repl-sessions.s2022-05-22
  "Went down a rabbit of hole of serializing/deserialing item-meta.
  The cool thing is that many objects implement a .serialize to map method, the
  not cool thing is that deserializing is a mess."
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.palette :as palette]
            [lambdaisland.witchcraft.matrix :as m]
            [net.arnebrasseur.cauldron.curves :as curves]))

(def me (wc/player "sunnyplexus"))

(wc/add-inventory me  {:material :elytra
                       :amount 1
                       :enchants {:unbreaking 3 :mending 1}})
(wc/add-inventory me :lapis-lazuli 64)
(.setExperience (wc/spawn (wc/add (wc/location (wc/player)) [0 3 0]) :experience-orb) 10000)
(wc/add-inventory me :enchanting-table)
(wc/add-inventory me :bookshelf 15)
(wc/add-inventory me :diamond-axe)
(wc/add-inventory me :gunpowder 64)
(wc/add-inventory me :paper 64)
(wc/add-inventory me :crafting-table)
(wc/get-inventory me)

(wc/set-time 1000)
(wc/clear-weather)

(wc/fly! me)

(def anchor [883 94 468])

(lambdaisland.witchcraft.safe-bean/bean (wc/item-meta (last (butlast (remove nil? (seq (wc/get-inventory me)))))))

(wc/contents me )

(org.bukkit.configuration.serialization.ConfigurationSerialization/deserializeObject

 (.serialize (wc/item-meta (last (butlast (remove nil? (seq (wc/get-inventory me))))))))

(.serialize (last (butlast (remove nil? (seq (wc/get-inventory me))))))

(defn as-map [o]
  (into {}
        (map (juxt (comp keyword key) val))
        (dissoc
         (into {}
               (.serialize o))
         "type"
         )))

(as-map (:meta (as-map (last (butlast (remove nil? (seq (wc/get-inventory me))))))))

(def fw-item-stack (last (butlast (remove nil? (seq (wc/get-inventory me))))))

(.newInstance (doto (.getDeclaredConstructor (.getClass (wc/-item-meta fw-item-stack)) (into-array Class [java.util.Map]))
                (.setAccessible true))
              (into-array Object [{"power" 2}]))

(defn deserialize [m klz]
  (.newInstance (doto (.getDeclaredConstructor klz (into-array Class [java.util.Map]))
                  (.setAccessible true))
                (into-array Object [m])))

(defn set-item-meta [o m]
  (let [im (wc/item-meta o)]
    (doto
        (org.bukkit.configuration.serialization.ConfigurationSerialization/deserializeObject
         (doto (into {}
                     (map (juxt (comp name key) val))
                     (merge (as-map im) m)) prn)
         (doto (class im) prn))
      prn)
    (wc/-set-item-meta o im)
    o
    ))

(set-item-meta (last (butlast (remove nil? (seq (wc/get-inventory me)))))
               {:power 2})

(org.bukkit.configuration.serialization.ConfigurationSerialization/deserializeObject
 {"meta-type" "FIREWORK"
  "power" 2}
 org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaFirework
 )
(org.bukkit.configuration.serialization.ConfigurationSerialization/getAlias
 org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaFirework
 )

(org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaItem$SerializableMeta/deserialize {"meta-type" "FIREWORK"
                                                                                       "power" (int 2)})


(map
 #(symbol (str "org.bukkit.craftbukkit.v1_18_R2.inventory." %))
 (keys

  {'CraftMetaArmorStand "ARMOR_STAND"
   'CraftMetaBanner "BANNER"
   'CraftMetaBlockState "TILE_ENTITY"
   'CraftMetaBook "BOOK"
   'CraftMetaBookSigned "BOOK_SIGNED"
   'CraftMetaSkull "SKULL"
   'CraftMetaLeatherArmor "LEATHER_ARMOR"
   'CraftMetaMap "MAP"
   'CraftMetaPotion "POTION"
   'CraftMetaSpawnEgg "SPAWN_EGG"
   'CraftMetaEnchantedBook "ENCHANTED"
   'CraftMetaFirework "FIREWORK"
   'CraftMetaCharge "FIREWORK_EFFECT"
   'CraftMetaKnowledgeBook "KNOWLEDGE_BOOK"
   'CraftMetaTropicalFishBucket "TROPICAL_FISH_BUCKET"
   'CraftMetaAxolotlBucket "AXOLOTL_BUCKET"
   'CraftMetaCrossbow "CROSSBOW"
   'CraftMetaSuspiciousStew "SUSPICIOUS_STEW"
   'CraftMetaEntityTag "ENTITY_TAG"
   'CraftMetaCompass "COMPASS"
   'CraftMetaBundle "BUNDLE"
   'CraftMetaItem "UNSPECIFIC"}))

(map
 #(.serialize (deserialize {} %))
 [org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaSkull
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaTropicalFishBucket
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaCharge
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaBookSigned
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaEnchantedBook
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaSuspiciousStew
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaBundle
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaBook
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaCompass
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaCrossbow
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaBlockState
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaMap
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaAxolotlBucket
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaBanner
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaItem
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaLeatherArmor
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaSpawnEgg
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaEntityTag
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaPotion
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaKnowledgeBook
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaFirework
  org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaArmorStand])

(update (into {}
              (.serialize
               (doto (deserialize {} org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaSkull)
                 (.setOwner "sunnyplexus"))))
        "skull-owner"
        #(.serialize %))
