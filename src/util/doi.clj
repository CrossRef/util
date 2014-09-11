(ns util.doi
  (:require [util.string :refer [add-leading remove-leading]]))

(def dx-doi-url "http://dx.doi.org/")

; Convert DOIs into canonical format (with a URL).
(def normalise-doi (partial add-leading dx-doi-url))

; Convert DOI into URL format.
(def non-url-doi (partial remove-leading dx-doi-url))

