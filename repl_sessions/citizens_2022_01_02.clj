(ns citizens-2022-01-02
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.events :as e]
            [lambdaisland.witchcraft.reflect :as reflect]
            [lambdaisland.witchcraft.util :as util])
  (:import (net.citizensnpcs Citizens)
           (net.citizensnpcs.api CitizensAPI)
           (net.citizensnpcs.api.trait Trait)
           (net.citizensnpcs.api.npc NPCRegistry)
           (net.citizensnpcs.npc CitizensTraitFactory)
           (org.bukkit.entity EntityType)))

(defn npc-registry ^NPCRegistry []
  (CitizensAPI/getNPCRegistry))

(defprotocol HasCreateNPC
  (-create-npc [_ e s]))

(reflect/extend-signatures HasCreateNPC
  "createNPC(org.bukkit.entity.EntityType,java.lang.String)"
  (-create-npc [this entity-type npc-name]
    (.createNPC this entity-type npc-name)))

(defn create-npc [entity-type npc-name]
  (-create-npc (npc-registry)
               (get wc/entity-types entity-type)
               npc-name))

(comment

  (def jonny (create-npc :player "jonny"))

  (def me (wc/player "sunnyplexus"))

  (wc/set-time 0)

  (wc/spawn (wc/in-front-of me) jonny)
)
