(ns util.doi
  (:require [util.string :refer [add-leading remove-leading]]))

(def dx-doi-url "http://dx.doi.org/")
(def doi-scheme "doi:")

; Convert DOIs into canonical format (with a URL).
(defn normalise-doi [doi]
    (add-leading dx-doi-url (remove-leading doi-scheme doi)))

; Convert DOI into URL format.
(defn non-url-doi [doi]
    (remove-leading dx-doi-url (remove-leading doi-scheme doi)))
