(ns repl-sessions.citizens-20220101
  (:require [lambdaisland.witchcraft.util :as util])
  (:import (net.citizensnpcs Citizens)
           (net.citizensnpcs.api CitizensAPI)
           (net.citizensnpcs.npc CitizensTraitFactory)
           (net.citizensnpcs.api.trait Trait)
           (org.bukkit.entity EntityType)
           (net.bytebuddy ByteBuddy)
           (net.bytebuddy.matcher ElementMatchers)
           (net.bytebuddy.implementation MethodDelegation)))

(defn citizens-instance []
  (.get (util/accessible-field CitizensAPI "instance")
        CitizensAPI))

(def original-trait-factory
  (.get (util/accessible-field Citizens "traitFactory")
        (citizens-instance)))

(defmacro deftrait [name & body]
  (let [base-class (class (proxy [Trait] [""]))]
    `(defn ~name []
       {:trait-class ~base-class}
       (proxy [~(symbol (.getName base-class)) clojure.lang.IMeta] [~(str name)]
         (meta)
         ~@body))))
gen-class
(deftrait my-trait
  (click [e] (println "Trait got click!"))
  (onAttach [] (println "Trait got attached!")))

(defn my-trait []
  (proxy [Trait] ["my-trait"]
    (click [e] (println "Trait got click!"))
    (onAttach [] (println "Trait got attached!"))))

(ancestors (class (my-trait)))

(def registry (atom [{:name "my-trait"
                      :class (class (my-trait))
                      :ctor #'my-trait
                      }]))

(def trait-factory
  (proxy [CitizensTraitFactory] []
    (getTrait [arg]
      (println "getTrait(" (class arg) "" arg ")")
      (some (fn [{:keys [class name ctor]}]
              (when (#{name class} arg)
                (ctor)))
            @registry))
    (getTraitClass [^String arg]
      (some (fn [{:keys [class name var]}]
              (when (#{name} arg)
                class))
            @registry))))

(util/set-field! Citizens "traitFactory" (citizens-instance)
                 trait-factory)



(defonce thief (.. CitizensAPI
                   getNPCRegistry
                   (createNPC EntityType/PLAYER "fullwall")))

(def t
  (proxy [Trait] ["my-trait"]
    (click [e] (println "Trait got click!"))
    (onAttach [] (println "Trait got attached!"))))

(.addTrait thief t)

(.getOrAddTrait thief
                (.getTraitClass trait-factory "my-trait"))

(.spawn thief (-> (wc/player)
                  (wc/location)))

(.. thief
    getNavigator
    (setTarget (-> (wc/player)
                   (wc/location))))

(comment (.. thief
             getDefaultGoalController
             (addGoal (WanderGoal/createWithNPC thief) 1)))

                                        ; A barebones version of TargetNearbyEntityGoal:
(def meanBehaviour
(proxy [Behavior] []
  (reset [])
  (run []
    BehaviorStatus/RUNNING)
  (shouldExecute []
    (if (.. thief isSpawned)
      (do (.. thief
              getNavigator
              (setTarget (wc/player) true))
          true)
      false))))

(.. thief
getDefaultGoalController
clear)

(.. thief
    getDefaultGoalController
    (addBehavior meanBehaviour 1))

(lambdaisland.classpath/update-classpath! {})

(def x
  (.. (net.bytebuddy.ByteBuddy.)
      (subclass Trait)
      (method (ElementMatchers/named "onAttach"))
      (intercept (MethodDelegation/to (fn [e] (prn "!!!" e))))
      (make)
      (load (lambdaisland.classpath/context-classloader))
      (getLoaded)))

(.onAttach
 (.newInstance
  (.getDeclaredConstructor x (into-array Class [String]))
  (into-array Object ["xxx"])))
x
(net.citizensnpcs.api.trait.Trait$ByteBuddy$tgl9PrRD. "xxx")

;;=> net.citizensnpcs.api.trait.Trait$ByteBuddy$dImbs8fA
