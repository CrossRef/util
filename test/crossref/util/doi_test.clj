(ns crossref.util.doi-test
  (:require [clojure.test :refer :all]
            [crossref.util.doi :refer :all]))

(deftest test-doi
  (testing "Normalise works"
  (is (= "http://dx.doi.org/10.5555/12345678" (normalise-doi "10.5555/12345678"))
      "Fixes un-normalised DOIs.")
  (is (= "http://dx.doi.org/10.5555/12345678" (normalise-doi "doi:10.5555/12345678"))
      "Fixes un-normalised DOIs with doi: scheme.")
  (is (= "http://dx.doi.org/10.5555/12345678" (normalise-doi "https://dx.doi.org/10.5555/12345678"))
      "Normalizes HTTPS to HTTP")
  (is (= "http://dx.doi.org/10.5555/12345678" (normalise-doi "https://dx.doi.org/10.5555/12345678"))
      "Normalizes HTTPS to HTTP")
  (is (= "http://dx.doi.org/10.5555/12345678" (normalise-doi "http://doi.org/10.5555/12345678"))
      "Normalizes doi.org to dx.doi.org")
  (is (= "http://dx.doi.org/10.5555/12345678" (normalise-doi "http://dx.doi.org/10.5555/12345678"))
      "Preserves normalised DOIs"))
  (is (= "http://dx.doi.org/10.5555/abc12345678"
         (normalise-doi "10.5555/AbC12345678")
         (normalise-doi "10.5555/aBc12345678"))
    "Normalizes case"))

(deftest test-non-url-doi
  (testing "Non-URL normalize works"
  (is (= "10.5555/12345678" (non-url-doi "10.5555/12345678"))
      "Preserves non-URLed DOIs")
  (is (= "10.5555/12345678" (non-url-doi "dx.doi.org/10.5555/12345678"))
      "Fixes DOIs with hostname")
  (is (= "10.5555/12345678" (non-url-doi "http://dx.doi.org/10.5555/12345678"))
      "Fixes URLed DOIs")
  (is (= "10.5555/12345678" (non-url-doi "https://dx.doi.org/10.5555/12345678"))
      "Fixes HTTPS URLed DOIs")
  (is (= "10.5555/12345678" (non-url-doi "http://doi.org/10.5555/12345678"))
      "Fixes URLs for doi.org proxy")
  (is (= "10.5555/abcdef123"
         (non-url-doi "http://doi.org/10.5555/abcdef123")
         (non-url-doi "http://doi.org/10.5555/AbCdEf123"))
      "Normalizes case")
  (is (= "10.5555/12345678" (non-url-doi "doi:10.5555/12345678"))
      "Fixes DOI schema")))

(deftest prefix
  (testing "Prefix can be obtained from all forms of DOI"
  (is (= "10.5555" (get-prefix "10.5555/12345678")))
  (is (= "10.5555" (get-prefix "dx.doi.org/10.5555/12345678")))
  (is (= "10.5555" (get-prefix "http://dx.doi.org/10.5555/12345678")))
  (is (= "10.5555" (get-prefix "https://dx.doi.org/10.5555/12345678")))
  (is (= "10.5555" (get-prefix "doi:10.5555/12345678")))))