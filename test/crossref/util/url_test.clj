(ns crossref.util.url-test
  (:require [clojure.test :refer :all]
            [crossref.util.url :refer :all]))

(deftest test-ensure-scheme
  (testing "ensure-scheme works")
  (is (= "http://www.crossref.org/tdm") (ensure-scheme "www.crossref.org/tdm"))
  (is (= "http://www.crossref.org/tdm") (ensure-scheme "http://www.crossref.org/tdm"))
  (is (= "http://www.crossref.org/tdm") (ensure-scheme "https://www.crossref.org/tdm")))