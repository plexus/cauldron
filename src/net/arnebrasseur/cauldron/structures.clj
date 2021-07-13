(ns net.arnebrasseur.cauldron.structures
  (:require [lambdaisland.witchcraft.cursor :as c]))

(defn lamppost [cursor]
  (-> cursor
      (c/face :up)
      (c/palette {\f :iron-fence
                  \C :cobblestone
                  \# :concrete
                  \t :trap-door
                  \* :fire
                  \N :netherrack})
      (c/pattern "CfffN*")))
