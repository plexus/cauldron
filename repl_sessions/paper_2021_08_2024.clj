(ns paper-2021-08-2024
  (:require [clojure.string :as str]
            [lambdaisland.witchcraft :as wc]
            [lambdaisland.witchcraft.shapes :as shapes]
            [lambdaisland.witchcraft.events :as e]
            [lambdaisland.witchcraft.cursor :as cursor]
            [lambdaisland.witchcraft.palette :as p]
            [lambdaisland.witchcraft.matrix :as m]))

(wc/add-inventory (wc/player "sunnyplexus") :anvil)

(clojure.reflect/reflect org.bukkit.entity.Player)

sendMessage
sendActionbar
setSubtitle
setPlayerListHeaderFooter
chat
showTitle
playNote
sendRawMessage

(wc/send-message (wc/player "sunnyplexus")
                 [:<>
                  [:obfuscated "**"]
                  " "
                  [:dark-red "spoookkkyyy"]
                  " "
                  [:obfuscated "**"]]
                 )

(.sendMessage (wc/player "sunnyplexus")
              (format
               ))

(.sendMessage (wc/player "sunnyplexus")
              (format
               [:reset
                [:red
                 "\n╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂"]
                [:black "\n.\n."]
                [:aqua "    Welcome to the Thunderdome"]
                [:black "\n."]
                [:red
                 "\n╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂╂\n\n"]
                [:black "\n."]]))


(.sendTitle (wc/player "sunnyplexus")
            (format [:blue "╪╪"
                     [:aqua " Prepare to DIE" ]
                     "╪╪"]) "")

(expand-markup [:blue
                "Hello"
                [:underline "under"
                 [:bold "bold"
                  [:green "green"]]]
                ])
