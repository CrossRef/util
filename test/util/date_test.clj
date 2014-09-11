(ns util.date-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as clj-time]
            [util.date :refer [pp crossref-date]]))


(def x (crossref-date 1986 21 2))
(def y (clj-time/date-time 2014 5 2))

(prn (clj-time/before? x y))
(prn (clj-time/before? y x))

(prn (pp x))