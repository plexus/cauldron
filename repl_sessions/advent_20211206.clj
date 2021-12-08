(ns advent-20211206
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.cursor :as cursor]
            [lambdaisland.witchcraft.events :as events]
            [lambdaisland.witchcraft.fill :as fill]
            [lambdaisland.witchcraft.markup :as markup]
            [lambdaisland.witchcraft.palette :as palette]
            [lambdaisland.witchcraft.shapes :as shapes]
            [lambdaisland.witchcraft.matrix :as matrix]))

(def me (wc/player "sunnyplexus"))
(wc/fly! me)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(wc/send-title me [:bold [:blue "Making things move"]])

(wc/set-blocks
 [[0 0 0 :slime-block]
  [-1 0 0 :sticky-piston :north]
  [0 1 0 :observer :east]
  [-1 0 -1 :slime-block]
  [0 0 -1 :sticky-piston :south]
  [-1 1 -1 :observer :west]]
 {:anchor [-16394 83 -17655]})

(wc/set-block [-16393 84 -17655] :cobblestone)

(future
  (dotimes [i 20]
    (wc/run-task
     #(wc/set-blocks
       [(wc/add {:x -16393 :y 84 :z -17655 :material :air} [i 0 0])
        (wc/add {:x -16393 :y 84 :z -17655 :material :cobblestone} [(inc i) 0 0])]))
    (Thread/sleep 250)))

(dotimes [i 20]
  (wc/set-blocks
   [(wc/add {:x -16393 :y 84 :z -17655 :material :air} [i 0 0])
    (wc/add {:x -16393 :y 84 :z -17655 :material :cobblestone} [(inc i) 0 0])])
  (Thread/sleep 250))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(wc/send-title me [:bold [:green "Recording macros"]])

(filter #(re-find #"block" (str %))
        (keys events/events))

(def recorded-blocks (atom []))

(events/listen!
 :block-place
 ::macro-record
 (fn [e]
   (swap! recorded-blocks conj (wc/block (:block e)))))

(defn recorded-structure []
  (map #(matrix/v- % (first @recorded-blocks)) @recorded-blocks))

@recorded-blocks
(recorded-structure)

(reset! recorded-blocks [])

(def lamp-post [{:x 0, :y 0, :z 0, :material :stripped-birch-wood, :block-data {:axis :y}}
                {:x 0,
                 :y 1,
                 :z 0,
                 :material :birch-fence,
                 :block-data
                 {:east false, :north false, :south false, :waterlogged false, :west false}}
                {:x 0,
                 :y 2,
                 :z 0,
                 :material :birch-fence,
                 :block-data
                 {:east false, :north false, :south false, :waterlogged false, :west false}}
                {:x 0,
                 :y 3,
                 :z 0,
                 :material :birch-fence,
                 :block-data
                 {:east false, :north false, :south false, :waterlogged false, :west false}}
                {:x 0,
                 :y 4,
                 :z 0,
                 :material :birch-fence,
                 :block-data
                 {:east false, :north false, :south false, :waterlogged false, :west false}}
                {:x -1,
                 :y 4,
                 :z 0,
                 :material :birch-fence,
                 :block-data
                 {:east true, :north false, :south false, :waterlogged false, :west false}}
                {:x -1,
                 :y 3,
                 :z 0,
                 :material :lantern,
                 :block-data {:hanging true, :waterlogged false}}])

(wc/set-blocks lamp-post
               {:start (wc/add (wc/target-block me) [0 1 0])})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(wc/send-title me
               [:bold [:gold "Implementing"]]
               [:bold [:gold "INTEROP"]])

wc/material
wc/get-inventory
wc/get-block

.getLocation

;; getter
(wc/location (wc/target-block (wc/player "sunnyplexus")))

;; coerces
(wc/location (wc/location (wc/target-block (wc/player "sunnyplexus"))))
(wc/location (wc/vec3 0 1 2))

;; clojure -> java
(wc/location [0 0 0])
(wc/location {:x 0 :y 1 :z 1})

gen-interface

(in-ns 'clojure.core)
(defmacro gen-interface
  [& options]
  (prn [:gen-interface clojure.lang.Compiler/LOADER])
  (let [options-map (apply hash-map options)
        [cname bytecode] (#'clojure.core/generate-interface options-map)]
    (when *compile-files*
      (clojure.lang.Compiler/writeClassFile cname bytecode))
    (.defineClass ^DynamicClassLoader (doto (deref clojure.lang.Compiler/LOADER) prn)
                  (str (:name options-map)) bytecode options)))

(ns not-user)
nil

(definterface IFoo)
not_user.IFoo

(macroexpand-1 '(definterface IFoo))
(clojure.core/let [] (clojure.core/gen-interface :name not_user.IFoo :methods []) (clojure.core/import not_user.IFoo))

(macroexpand '(definterface IFoo))
(let* [] (clojure.core/gen-interface :name not_user.IFoo :methods []) (clojure.core/import not_user.IFoo))

(clojure.walk/macroexpand-all '(definterface IFoo))
(let* [] not_user.IFoo (do (clojure.core/import* "not_user.IFoo")))
