(ns crossref.util.auth
    (:require [clojure.tools.logging :refer [info error]])
    (:require [clj-http.client :as client])
    (:require [clojure.data.json :as json]))
  

(def auth-endpoint "https://apps.crossref.org/auth/")

(defn authenticate
  "Authenticate with CrossRef member username and password and return the prefixes that user is allowed to administer. Returns vector of [authenticated? prefixes is-admin]."
  [username password]
  (let [response (client/get auth-endpoint {:method :get
                                            :basic-auth [username password]
                                            :insecure? true ; FIXME: CrossRef certificate isn't in this JVM's keystore. 
                                            :throw-exceptions false})]
    (if (not= (:status response) 200)
      [false [] false]
      (if-let [body (:body response)]
        (let [{prefixes :prefixes is-admin :is-admin} (json/read-str body :key-fn keyword)]
          [(or is-admin (not (empty? prefixes))) prefixes is-admin])
        [false [] false]))))