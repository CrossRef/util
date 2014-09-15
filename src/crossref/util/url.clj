(ns crossref.util.url)

(defn http-scheme? [url]
  (or (.startsWith url "http://")
      (.startsWith url "https://")))

(defn ensure-scheme
  "Ensure an almost-valid URL has a scheme."
  [url] (if (http-scheme? url) url (str "http://" url)))