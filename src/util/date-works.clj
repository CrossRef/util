(ns util.date
  (:require [clj-time.core :as clj-time])
  (:require [clj-time.core :refer [DateTimeProtocol]])
  )

; Dates based on clj-time, but that handle all dates available in the Weird schema.

(defprotocol IWeirdDate
  (as-date [this]))

(deftype WeirdDate [year month day]
  IWeirdDate
  (as-date [this] (clj-time/date-time year month day)))

(extend-protocol DateTimeProtocol
    WeirdDate
    (year [this] (clj-time/year (as-date this)))
    (month [this] (clj-time/month (as-date this)))
    (day [this] (clj-time/day (as-date this)))
    (day-of-week [this] (clj-time/day-of-week (as-date this)))
    (hour [this] (clj-time/hour (as-date this)))
    (minute [this] (clj-time/minute (as-date this)))
    (sec [this] (clj-time/sec (as-date this)))
    (second [this] (clj-time/second (as-date this)))
    (milli [this] (clj-time/milli (as-date this)))
    (after? [this that] (clj-time/after? (as-date this) that))
    (before? [this that] (clj-time/before? (as-date this) that)))

(defn weird-date [y m d] (WeirdDate. y m d))

(def x (WeirdDate. 1986 5 2))
(def y (clj-time/date-time 2014 5 2))

(prn "Loaded OK source" x y)
