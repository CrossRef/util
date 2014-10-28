(ns crossref.util.auth
    (:require [clojure.tools.logging :refer [info error]])
    (:require [clj-http.client :as client])
    (:require [clojure.data.json :as json]))
  

(def auth-endpoint "https://apps.crossref.org/auth/")

(def test-mode (atom false))

(defn set-test-mock-mode!
  "Enable or disable test mock mode."
  [is-test]
  (reset! test-mode is-test))

(def test-mock-prefixes
  ["10.9999" "10.8888" "10.7777" "10.6666" "10.5555" "10.4444" "10.3333" "10.2222" "10.1111"])

(def test-mock-data
  {["testuser0" "testpassword0"] [(take 0 test-mock-prefixes) false]
   ["testuser1" "testpassword1"] [(take 1 test-mock-prefixes) false]
   ["testuser2" "testpassword2"] [(take 2 test-mock-prefixes) false]
   ["testuser3" "testpassword3"] [(take 3 test-mock-prefixes) false]
   ["testuser4" "testpassword4"] [(take 4 test-mock-prefixes) false]
   ["testuser5" "testpassword5"] [(take 5 test-mock-prefixes) false]
   ["testuser6" "testpassword6"] [(take 6 test-mock-prefixes) false]
   ["testuser7" "testpassword7"] [(take 7 test-mock-prefixes) false]
   ["testuser8" "testpassword8"] [(take 8 test-mock-prefixes) false]
   ["testuser9" "testpassword9"] [(take 9 test-mock-prefixes) false]
   ["adminuser" "adminpassword"] [test-mock-prefixes true]})

(defn authenticate-mock
  [username password]
  (let [user-credentials (test-mock-data [username password])]
    (if user-credentials
      [true (first user-credentials) (second user-credentials)]
      [false [] false])))

(defn authenticate-real
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
          [(or is-admin (not (nil? prefixes))) prefixes is-admin])
        [false [] false]))))

(defn authenticate
  [username password]
  (if @test-mode (authenticate-mock username password)
                 (authenticate-real username password)))