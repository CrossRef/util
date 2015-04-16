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

(deftest prefix
  (testing "Suffix can be obtained from all forms of DOI"
  (is (= "12345678" (get-suffix "10.5555/12345678")))
  (is (= "12345678" (get-suffix "dx.doi.org/10.5555/12345678")))
  (is (= "12345678" (get-suffix "http://dx.doi.org/10.5555/12345678")))
  (is (= "12345678" (get-suffix "https://dx.doi.org/10.5555/12345678")))
  (is (= "12345678" (get-suffix "doi:10.5555/12345678"))))
  
  (testing "Suffix can include slashes"
    (is (= "11/22/33/44/55/66/" (get-suffix "10.5555/11/22/33/44/55/66/")))
    (is (= "11/22/33/44/55/66/" (get-suffix "http://dx.doi.org/10.5555/11/22/33/44/55/66/")))))

(deftest test-well-formed
  (testing "nil DOI is not well formed"
    (is (not (well-formed nil))))
  
  (testing "empty string DOI is not well formed"
    (is (not (well-formed ""))))
  
  (testing "arbitrary string is not well formed"
    (is (not (well-formed "dee ex dot dee oh eye dot org slash ten dot five five five five slash one two three four five six seven eight"))))
  
  (testing "just a prefix is not well formed"
    (is (not (well-formed "10.5555")))
    (is (not (well-formed "10.5555/")))
    (is (not (well-formed "dx.doi.org/10.5555"))))
  
  (testing "just a suffix is not well formed"
    (is (not (well-formed "/12345678")))
    (is (not (well-formed "123456789"))))
  
  (testing "non-doi-prefix is not well formed"
    (is (not (well-formed "11.5555/12345678")))
    (is (not (well-formed "dx.doi.org/11.5555/12345678"))))
  
  (testing "correct DOI in all formats is well-formed"  
    (is (well-formed "10.5555/12345678"))
    (is (well-formed "dx.doi.org/10.5555/12345678"))
    (is (well-formed "http://dx.doi.org/10.5555/12345678"))
    (is (well-formed "https://dx.doi.org/10.5555/12345678"))
    (is (well-formed "doi:10.5555/12345678")))
  
  (testing "DOI with non-numerical prefix is well-formed"  
    (is (well-formed "10.ABCDEFG/12345678")))
  
  (testing "Examples from ANSI/NISO Z39.84-2000 are well-formed"  
    ; Appendix C
    (is (well-formed "10.054/1418EC1N2LE"))
    (is (well-formed "10.1002/(SICI)1097-4571(199806)49:8<693::AID-ASI4>3.0.CO;2-O"))
    (is (well-formed "10.1001/PUBS.JAMA(278)3,JOC7055-ABST:"))
    (is (well-formed "10.1006/rwei.1999.0001"))
    
    ; Appendix E
    (is (well-formed "http://dx.doi.org/10.1006/rwei.1999.0001")))
  
  (testing "DOI suffix can't start with «single-character»/ (Z39.84-2000 §4.3)"
    (is (not (well-formed "10.5555/1/2345678"))
    (is (not (well-formed "dx.doi.org/10.5555/1/2345678"))))))