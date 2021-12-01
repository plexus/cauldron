(ns dependencies
  (:require [lambdaisland.classpath :as licp]
            [clojure.java.io :as io] :reload))

(licp/debug!)
(licp/update-classpath! '{:extra
                          {:deps {javax.servlet/javax.servlet-api {:mvn/version "3.1.0"}}}})

(licp/classpath-chain)

(require '[lambdaisland.classpath :as licp] :reload)

(println (slurp (io/resource "lambdaisland/classpath.clj")))
