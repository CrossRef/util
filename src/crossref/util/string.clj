(ns crossref.util.string)

(defn remove-leading [prefix string]
  (if (.startsWith string prefix)
    (apply str (drop (count prefix) string))
    string))

(defn add-leading [prefix string]
  (if (.startsWith string prefix)
    string
    (str prefix string)))

(defn md5
  "Generate a md5 checksum for the given string"
  [token]
  (let [hash-bytes
         (doto (java.security.MessageDigest/getInstance "MD5")
               (.reset)
               (.update (.getBytes token)))]
       (.toString
         (new java.math.BigInteger 1 (.digest hash-bytes)) ; Positive and the size of the number
         16)))

(defn parse-int
  "Parse int with other text in it."
  [input]
  (. Integer parseInt (apply str (filter #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9} input))))
