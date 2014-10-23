(ns crossref.util.date-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as clj-time]
            [clj-time.format :as clj-time-format]
            [crossref.util.date :as crd]))

(deftest date
  (testing "Seasons in order")
  (let [spring-2010 (crd/crossref-date 2010 crd/spring)
        summer-2010 (crd/crossref-date 2010 crd/summer)
        autumn-2010 (crd/crossref-date 2010 crd/autumn)
        winter-2010 (crd/crossref-date 2010 crd/winter)
        spring-2011 (crd/crossref-date 2011 crd/spring)]
    (is (clj-time/before? spring-2010 summer-2010))
    (is (clj-time/before? summer-2010 autumn-2010))
    (is (clj-time/before? autumn-2010 winter-2010))
    (is (clj-time/before? winter-2010 spring-2011))
    
    (is (clj-time/after? summer-2010 spring-2010))
    (is (clj-time/after? autumn-2010 summer-2010))
    (is (clj-time/after? winter-2010 autumn-2010))
    (is (clj-time/after? spring-2011 winter-2010))))

(deftest date
  (testing "Quarters in order")
  (let [first-quarter-2010 (crd/crossref-date 2010 crd/first-quarter)
        second-quarter-2010 (crd/crossref-date 2010 crd/second-quarter)
        third-quarter-2010 (crd/crossref-date 2010 crd/third-quarter)
        fourth-quarter-2010 (crd/crossref-date 2010 crd/fourth-quarter)
        first-quarter-2011 (crd/crossref-date 2011 crd/first-quarter)]
    (is (clj-time/before? first-quarter-2010 second-quarter-2010))
    (is (clj-time/before? second-quarter-2010 third-quarter-2010))
    (is (clj-time/before? third-quarter-2010 fourth-quarter-2010))
    (is (clj-time/before? fourth-quarter-2010 first-quarter-2011))
    
    (is (clj-time/after? second-quarter-2010 first-quarter-2010))
    (is (clj-time/after? third-quarter-2010 second-quarter-2010))
    (is (clj-time/after? fourth-quarter-2010 third-quarter-2010))
    (is (clj-time/after? first-quarter-2011 fourth-quarter-2010))))

(deftest partial-construction
  (testing "Dates can be constructed with various levels of accuracy.")  
  (is (= (crd/crossref-date 2010) (clj-time/date-time 2010 01 01)))
  (is (= (crd/crossref-date 2010 10) (clj-time/date-time 2010 10 01)))
  (is (= (crd/crossref-date 2010 10 15) (clj-time/date-time 2010 10 15)))
  
  (is (= (crd/crossref-date 2010 crd/second-quarter) (clj-time/date-time 2010 4 01))))



(deftest pretty-printing
  (testing "Dates can be printed with the accuracy and inputs they were constructed with")
  
  (is (= "2010" (str (crd/crossref-date 2010))))
  (is (= "2010-05" (str (crd/crossref-date 2010 5))))
  (is (= "2010-05-07" (str (crd/crossref-date 2010 5 7))))
  
  (is (= "Spring 2010" (str (crd/crossref-date 2010 crd/spring))))
  
  ; Spring is just a constant (derived from the CrossRef deposit schema).
  (is (= "Spring 2010" (str (crd/crossref-date 2010 21))))
  
  (is (= "First quarter of 2010" (str (crd/crossref-date 2010 crd/first-quarter)))))

(deftest normal-date-things
  (testing "A CrossRef date is a normal date and can be used with clj-time functions"
    
    (is (= (clj-time/within? (clj-time/interval (clj-time/date-time 1986) (clj-time/date-time 1990))
                             (crd/crossref-date 1990 crd/first-quarter)))
        "Can use the clj-time within? function")))

(deftest parse-date
  (testing "Standard normal ISO8601 dates can be parsed."
  (is (= (clj-time-format/parse "2014-06-07") (crd/parse "2014-06-07"))))
  
  (testing "Special dates that look like ISO8601 can be parsed."
  (is (= (clj-time-format/parse "2014-03-01") (crd/parse "2014-21-01"))
      "Spring equates to first day of spring"))
  
  (testing "Dates with slashes can be parsed"
  (is (= (clj-time-format/parse "1066-10-14") (crd/parse "1066/10/14"))))

  (testing "Partial dates can be parsed"
    (is (= (crd/parse "2014") (crd/crossref-date 2014)))
    (is (= (crd/parse "2014/10") (crd/crossref-date 2014 10)))
    (is (= (crd/parse "2014/10/14") (crd/crossref-date 2014 10 14)))
    (is (= (crd/parse "2014") (crd/crossref-date 2014)))
    (is (= (crd/parse "2014-10") (crd/crossref-date 2014 10)))
    (is (= (crd/parse "2014-10-14") (crd/crossref-date 2014 10 14)))))