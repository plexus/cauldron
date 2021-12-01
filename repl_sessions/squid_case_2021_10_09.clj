(ns squid-case-2021-10-09
  (:require [lambdaisland.witchcraft :as wc]
            [clojure.reflect :as reflect]
            [lambdaisland.witchcraft.markup :as markup]
            [lambdaisland.witchcraft.events :as e]
            [lambdaisland.witchcraft.util :as util]))

(def display-slot
  (util/enum->map org.bukkit.scoreboard.DisplaySlot))

(def render-type
  (util/enum->map org.bukkit.scoreboard.RenderType))

(defn scoreboard-manager []
  (org.bukkit.Bukkit/getScoreboardManager))

(defn new-scoreboard []
  (.getNewScoreboard (scoreboard-manager)))

(defn objective ^org.bukkit.scoreboard.Objective [board obj-name criterion]
  (let [name (name obj-name)]
    (or (.getObjective board name)
        (.registerNewObjective board name (or criterion "dummy")))))

(defn set-scores
  "Declarative API for setting scores on scoreboars.

  Takes a map with objectives. Normally you would have one objective for each
  slot you want to use (`:sidebar`, `:player-list`, `:below-name`). You can also
  use different objective names and explicitly set their `:slot`.

  For each objective you can provide
  - `:display` the title, can be a markup vector
  - `:scores` a map of scores for entries, can be a player name or something
    else. Vectors will be formatted. Instead of a map you can provide a
    list/vector, in which case these will be given decreasing scores so they sort
    top to bottom, handy if you want to use a sidebar more as a generic text area
  - `:criterion` for automatic score keeping by the game, e.g. `\"health\"` or
    `\"totalKillcount\"`, see https://minecraft.fandom.com/wiki/Scoreboard
  - `:type` the render type for `:player-list` objectives, show as `:hearts` or
    `:integer`

  ```
  (set-scores
    board
    {:sidebar {:display (fAnCy \"Scoreboard\" [:red :gold :yellow])
               :scores [\"---------------------\"
                        [:blue \"Hello\"]
                        \"--------------------\"]}
     :player-list {:scores {\"sunnyplexus\" 5}
                   :type :integer}
     :below-name {:criterion \"totalKillCount\"
                  :display [:red \"Kills\"]}})
  ```
  "
  [board m]
  (let [entries (.getEntries board)]
    (run! #(.resetScores board ^String %)
          (remove
           (set
            (for [v (vals m)
                  :let [scores (:scores v)]
                  k (if (sequential? scores) scores (keys scores))]
              (markup/render k)))
           entries)))
  (doseq [[obj-name {:keys [slot display criterion scores type]
                     :as opts}] m]
    (let [obj (objective board obj-name criterion)
          slot (get display-slot (or slot obj-name))
          display (when display (markup/render display))]
      (when (and slot (not= slot (.getDisplaySlot obj)))
        (.setDisplaySlot obj slot))
      (when (and display (not= display (.getDisplayName obj)))
        (.setDisplayName obj display))
      (when (and criterion (not= (name criterion) (.getCriteria obj)))
        (throw (ex-info "Can't change objective criterion after it's created"
                        {:objective opts
                         :got (.getCriteria obj)})))
      (when-let [render-type (and type (get render-type type))]
        (when (not= render-type (.getRenderType obj))
          (.setRenderType obj render-type)))
      (when scores
        (let [scores (if (sequential? scores)
                       (into {}
                             (map-indexed (fn [idx k] {k (inc idx)}))
                             (reverse scores))
                       scores)]
          (doseq [[k v] scores]
            (.setScore (.getScore obj (markup/render k)) v)))))))


(def board (new-scoreboard))
(.setScoreboard (wc/player "sunnyplexus") board)

render-type

(defn fAnCy [string colors]
  (into [:<>]
        (map (fn [ch c]
               [c (str ch)])
             string (cycle colors))))

(set-scores
 board
 {:sidebar {:display (fAnCy "Scoreboard" [:red :gold :yellow])
            :scores ["---------------------"
                     [:blue "Hello"]
                     "--------------------"]}
  :player-list {:scores {"sunnyplexus" 5}
                :type :integer}
  :below-name {:criterion "totalKillCount"
               :display [:red "Kills"]}}
 )

(defn neighbours
  "Find all neighbours of a block along the given axes, defaults to :x/:z, i.e.
  neighbours in a horizontal plane."
  ([loc]
   (neighbours loc nil))
  ([loc {:keys [dx dy dz pred]
         :or {dx [-1 0 1]
              dy [0]
              dz [-1 0 1]
              pred #(not= (wc/material-name %) :air)}}]
   (for [dx dx dy dy dz dz
         nloc [(wc/add (wc/location loc) [dx dy dz])]
         :when (not= (wc/location loc) nloc)
         block [(wc/get-block nloc)]
         :when (pred block)]
     block)))

(defn fill
  "Recursively find neighbours"
  ([start]
   (fill start nil))
  ([start opts]
   (loop [search #{start}
          result #{start}]
     (let [new-blocks (reduce
                       (fn [res loc]
                         (into res (remove result) (neighbours loc opts)))
                       #{}
                       search)]
       (if (seq new-blocks)
         (recur new-blocks (into result new-blocks))
         result)))))

(defn normalize-text [txt]
  (wc/display-name
   (doto (wc/item-stack :wooden-axe 1)
     (wc/set-display-name txt))))

(def megachop-9000 (normalize-text
                    (markup/render
                     (fAnCy "MegaChop 9000"
                            [:yellow :gold :red]))))

(wc/display-name(wc/item-in-hand (wc/player)))

(prn megachop-9000)

(e/listen! :player-interact
           ::megachop-9000
           (fn [{:keys [clickedBlock player action]}]
             (when clickedBlock
               (let [material (wc/material-name clickedBlock)]
                 (prn (= megachop-9000 (doto (wc/display-name (wc/item-in-hand player)) prn))
                      (re-find #"-log$" (name material)))
                 (when (and (= megachop-9000 (wc/display-name (wc/item-in-hand player)))
                            (re-find #"-log$" (name material)))
                   (run! (memfn breakNaturally)
                         (fill clickedBlock {:pred #(do (prn %)
                                                        (= material (doto (wc/material-name %) prn)))
                                             :dy [-1 0 1]})))))

             ))


(def is (first (seq (wc/inventory (wc/player "sunnyplexus")))))

(wc/item-in-hand (wc/player "sunnyplexus"))

(let [m (.getItemMeta is)]
  (.setDisplayName m (markup/render (fAnCy "MegaChop 9000"
                                           [:yellow :gold :red])))
  (.setLore m
            ["Even the trees shivered"
             "when the Themjer passed."])
  (.setItemMeta is m))
