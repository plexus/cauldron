(ns repl-sessions.private-chest
  (:require [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.events :as e]
            [clojure.string :as str]))

(wc/player "sunnyplexus")

(wc/add-inventory (wc/player "sunnyplexus") :anvil)
(wc/add-inventory (wc/player "sunnyplexus") :chest)
(wc/add-inventory (wc/player "sunnyplexus") :golden-carrot 64)
(wc/add-inventory (wc/player "sunnyplexus") :paper 64)
(wc/add-inventory (wc/player "sunnyplexus") :diamond-axe)
(wc/add-inventory (wc/player "sunnyplexus") :stick 5)

(.giveExp (wc/player "sunnyplexus") 1000)

(filter #(re-find #"inventory" (str %)) (keys e/events))

(doseq [e '(:inventory-click
            ;; :inventory-close
            ;; :inventory-move-item
            ;; :inventory-drag
            ;; :inventory
            ;; :inventory-open
            ;; ;;:inventory-creative
            ;; :inventory-pickup-item
            ;; :inventory-interact
            )]
  (e/listen! e ::x #(do (def x %)
                        (prn [e %]))))

(e/unlisten! :inventory-creative ::x)
org.bukkit.

(do
  (e/listen! :player-name-entity ::x #(def a %))
  (e/listen! :inventory-move-item ::x #(def b %))
  (e/listen! :block-break ::x #(def c %)))
a
b
c
(wc/set-time 0)

(wc/-display-name (:player c))

(.getCustomName (.getState (:block c)))

(clojure.reflect/reflect (:block c))

(let [^org.bukkit.block.Chest chest (.getState (:block c))]
  (.getCustomName chest))

(:block c)


(wc/display-name (wc/get-block r))

(wc/display-name (wc/player))

(wc/inventory (wc/player))

(wc/player r)
(meta (bean r))

(meta (with-meta (bean r) {::event r}))

(assoc (bean r) :x :y)

(wc/game-mode)
(map :display-name (:contents (wc/inventory (:clickedInventory x))))


(/ 3 3/16)

(/ 3/16 3)

(bean (:currentItem x))

(price-list (:clickedInventory x))

(wc/display-name (:clickedInventory x))


(wc/add-inventory (wc/player "sunnyplexus") :diamond 35)
(wc/add-inventory  (:clickedInventory x) :golden-carrot 32)

(as-material-name "Golden Carro")

wc/materials

(defn as-material-name [s]
  (let [ms (str/replace (str/trim (str/lower-case s)) #"\s+" "-")
        m  (subs ms 0 (dec (count ms)))
        msk (keyword ms)
        mk (keyword m)]
    (cond
      (get wc/materials msk)
      msk
      (get wc/materials mk)
      mk)))

(defn price-list [inventory]
  (into {}
        (keep (fn [{:keys [display-name]}]
                (when display-name
                  (when-let [[_ a b c d] (re-find #"^(\d+)\s+([^:]+)?:\s*(\d+)\s+(.*)\s*$" display-name)]
                    (prn a b c d)
                    [(as-material-name b)
                     [(/ (Long/parseLong c)
                         (Long/parseLong a))
                      (as-material-name d)]]))))
        (:contents (wc/inventory inventory))))

(e/listen-raw!
 :block-break
 ::r
 (fn [e]
   (let [player-name (wc/display-name (wc/player e))
         block-name (wc/display-name (wc/get-block e))]
     (when (not (.startsWith block-name (str "@" player-name)))
       (e/cancel! e)))))

(defn other-player-inv? [inv player]
  (let [inv-name    (wc/display-name (wc/get-block inv))
        player-name (str "@" (wc/display-name player))]
    (and (= \@ (first inv-name))
         (not (or (= inv-name player-name)
                  (.startsWith inv-name (str player-name " "))
                  (.startsWith inv-name (str player-name "'")))))))

(defn deny! [e]
  (.setResult e org.bukkit.event.Event$Result/DENY))

(e/listen!
 :inventory-click
 ::r
 (fn [x]
   (let [inv             (:clickedInventory x)
         inv-inv         (wc/inventory inv)
         player          (:whoClicked x)
         player-inv      (wc/inventory (:whoClicked x))
         price-list      (price-list inv)
         clicked-item    (:currentItem x)
         clicked-amount  (.getAmount clicked-item)
         clicked-in-top? (= (:inventory x) (:clickedInventory x))]
     (when (other-player-inv? (:inventory x) player)
       (cond
         ;; This could be fine tuned, only allow double click if clicking in the
         ;; bottom and no matching items in the top, but just canceling it for
         ;; now.
         (= (:click x) org.bukkit.event.inventory.ClickType/DOUBLE_CLICK)
         (e/cancel! x)

         clicked-in-top?
         (do
           (e/cancel! x)
           (when (and )(= (wc/material-name (:cursor x)) :air)
                 (when-let [[coin-amount coin-type] (get price-list (wc/material-name (:currentItem x)))]
                   (let [total-price   (Math/ceil (* clicked-amount coin-amount))
                         not-removed   (wc/remove-inventory player coin-type total-price)
                         removed-count (- total-price (transduce (map #(.getAmount %)) + 0 (vals not-removed)))
                         paid-for      (Math/floor (/ removed-count coin-amount))]
                     (wc/remove-inventory inv (wc/material clicked-item) paid-for)
                     (wc/add-inventory player (wc/material clicked-item) paid-for)
                     (wc/add-inventory inv coin-type removed-count))))))))))

(e/listen!
 :inventory-drag
 ::r
 (fn [x]
   (let [inv    (:inventory x)
         player (:whoClicked x)]
     (when (and (other-player-inv? inv player)
                (some #(< % (doto (.getSize inv) prn)) (doto (keys (:newItems x)) prn)))
       (e/cancel! x)))))

x
