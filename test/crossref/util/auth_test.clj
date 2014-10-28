(ns crossref.util.auth-test
  (:require [clojure.test :refer :all]
            [crossref.util.auth :refer :all]))

(testing "Can't use mock credentials in normal mode"
  (let [[authenticated prefixes admin] (authenticate "testuser0" "testpassword0")]
    (is (false? authenticated))
    (is (empty? prefixes))
    (is (false? admin))))


(testing "Can use mock credentials in test mode"
  (set-test-mock-mode! true)
  (let [[authenticated prefixes admin] (authenticate "testuser3" "testpassword3")]
    (is (true? authenticated))
    (is (= (count prefixes) 3))
    (is (false? admin))))

(testing "Can use mock admin credentials in test mode"
  (set-test-mock-mode! true)
  (let [[authenticated prefixes admin] (authenticate "adminuser" "adminpassword")]
    (is (true? authenticated))
    (is (= (count prefixes) 9))
    (is (true? admin))))