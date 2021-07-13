(ns net.arnebrasseur.cauldron.roll-tables)

(defn stone-paving []
  (let [n (rand-int 100)]
    (cond
      (<= 0 n 50) [:stone 0]
      (<= 51 n 77) [:cobblestone 0]
      (<= 78 n 80) [:stone 4]
      (<= 81 n 93) [:stone 6]
      (<= 93 n 95) [:stone 3]
      (<= 96 n 100) [:gravel])))
