(ns crossref.util.doi
  (:require [crossref.util.string :refer [add-leading remove-leading]]))

(def dx-doi-url "http://dx.doi.org/")
(def doi-scheme "doi:")

(defn normalise-doi
  "Convert DOIs into canonical format (with a URL)."
  [doi]
  (add-leading dx-doi-url (remove-leading doi-scheme doi)))

(defn non-url-doi
  "Convert DOI into URL format."
  [doi]
  (let [to-drop ["http://" "https://" "dx.doi.org/" "doi:"]
        ; Remove prefixes one by one
        without-prefixes (reduce (fn [v prefix] (if (.startsWith v prefix) (.substring v (count prefix)) v)) doi to-drop)]
    without-prefixes))

(defn get-prefix [doi]
  (let [split (.split (non-url-doi doi) "/")]   
  (first split)))