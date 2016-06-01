(ns crossref.util.doi
  (:require [crossref.util.string :refer [add-leading remove-leading]])
  (:require [clojure.string]))

(def dx-doi-url "https://doi.org/")

(defn non-url-doi
  "Convert DOI into non-URL format."
  [doi]
  (let [to-drop ["http://" "https://" "dx.doi.org/" "doi.org/" "doi:"]
        ; Remove prefixes one by one
        without-prefixes (reduce (fn [v prefix] (if (.startsWith v prefix) (.substring v (count prefix)) v)) doi to-drop)]
    (.toLowerCase without-prefixes)))

(defn normalise-doi
  "Convert DOIs into canonical format (with a URL)."
  [doi]
  (add-leading dx-doi-url (non-url-doi doi)))

(defn get-prefix [doi]
  (let [split (.split (non-url-doi doi) "/")]   
  (first split)))

(defn get-suffix [doi]
  (let [doi (non-url-doi doi)
        prefix (get-prefix doi)]
    (when-not (or
                (clojure.string/blank? prefix)
                ; If there's nothing left, there's no suffix
                ; (and substring won't work in any case).
                (= doi prefix))
      (.substring doi (inc (.length prefix))))))

(defn well-formed [doi]
  ; According to ANSI/NISO-DOI-Z39-84-2000
  (if (clojure.string/blank? doi)
    false
    (let [prefix (get-prefix doi)
          suffix (get-suffix doi)
          ; §4.2
          valid-directory-code (.startsWith prefix "10.")
          ; One or more character after the directory code and the stop.
          valid-registrant-code (> (.length prefix) 3)
          
          ; Must be at least 1-length.
          long-enough-suffix (not (clojure.string/blank? suffix))
          
          ; § 4.3
          valid-suffix (and suffix (not (re-find #"./" suffix)))]      
      (and valid-directory-code valid-registrant-code long-enough-suffix valid-suffix))))