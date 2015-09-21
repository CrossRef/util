(ns crossref.util.config
    (:require [clojure.edn :as edn])    
    (:require [clojure.tools.logging :refer [info error]]))
  
; List of files to try opening, in order.
(def config-files ["config.edn" "config.dev.edn"])

(def first-extant-file (first (filter #(.exists (clojure.java.io/as-file %)) config-files)))

(def config
  (let [file-to-use first-extant-file]
    (if-not file-to-use
      (error "Can't find config file")
      (let [config-file (slurp file-to-use)
            the-config (edn/read-string config-file)]
            (info "Using config file" first-extant-file "with keys" (keys the-config))
            the-config))))
