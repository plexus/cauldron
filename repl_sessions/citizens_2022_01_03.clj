(ns repl-sessions.citizens-2022-01-03
  (:require [clojure.string :as str]
            [lambdaisland.witchcraft.util :as util]
            [lambdaisland.classpath :as licp])
  (:import (net.citizensnpcs Citizens)
           (net.citizensnpcs.api CitizensAPI)
           (net.citizensnpcs.npc CitizensTraitFactory)
           (net.citizensnpcs.api.trait Trait TraitFactory)
           (org.bukkit.entity EntityType)))

(defn trait-factory ^TraitFactory []
  (CitizensAPI/getTraitFactory))

(defn gen-and-load-class
  "Generates and immediately loads the bytecode for the specified
  class. Note that a class generated this way can be loaded only once
  - the JVM supports only one class with a given name per
  classloader. Subsequent to generation you can import it into any
  desired namespaces just like any other class. See gen-class for a
  description of the options.

  Taken from the rich comment section of `clojure/genclass.clj`.
  "
  [options-map]
  (let [[cname bytecode] (#'clojure.core/generate-class options-map)]
    (prn cname)
    (.defineClass (licp/root-loader)
                  (str/replace cname
                               #"/"
                               ".")
                  bytecode
                  "")))

(defn trait-load [this key])
(defn trait-onAttach [this])
(defn trait-onCopy [this])
(defn trait-onDespawn [this])
(defn trait-onPreSpawn [this])
(defn trait-onSpawn [this])
(defn trait-run [this])
(defn trait-save [this key])

(defn define-trait [name callbacks]
  (in))

(gen-and-load-class
 {:name `test3
  :extends Trait
  :state "state"
  :init "init"
  :constructors {[] [String]}
  ;;:post-init `my-post-init
  :prefix "trait-"
  ;;:impl-ns "my.trait.impl"
  :load-impl-ns false
  })

(.state
 (repl-sessions.citizens-2022-01-03.test3.))

(make-trait "my-trait" {:on-attach (fn [this]
                                     (prn "attached!"))})

(.getTrait (trait-factory) "my-trait")

(.registerTrait (trait-factory)
                (.withName (TraitInfo/create lambdaisland.witchcraft.citizens.trait.my-trait) "my-trait"))

(.getTraitClass
 (.withName (TraitInfo/create lambdaisland.witchcraft.citizens.trait.my-trait) "my-trait"))

(.onAttach
 (.tryCreateInstance (.withName (TraitInfo/create lambdaisland.witchcraft.citizens.trait.my-trait) "my-trait")))

(seq
 (.getRegisteredTraits (trait-factory)))

(def traits
  (into {}
        (.get (util/accessible-field CitizensTraitFactory "registered") (trait-factory))))

(get traits "my-trait")
