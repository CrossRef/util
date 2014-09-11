(ns util.date
  (:require [clj-time.core :as clj-time]
            [clj-time.format :as f])
  (:require [clj-time.core :refer [DateTimeProtocol]])
  (:import (org.joda.time ReadableInstant )))

; Dates based on clj-time, but that handle all dates available in the CrossRef schema.
; These special values come from the CrossRef deposit schema.
; http://www.crossref.org/help/schema_doc/4.3.4/4_3_4.html#month

; The CrossRefDate type implements (among other things) the ReadableInstant Java interface. 
; This has a `get` method that clashes the supertype of objects created with Clojure's defrecord.
; Ergo it's a type not a record.

(def spring 21)
(def summer 22)
(def autumn 23)
(def winter 24)

(def first-quarter 31)
(def second-quarter 32)
(def third-quarter 33)
(def fourth-quarter 34)

(defprotocol ICrossRefDate
  "A CrossRef date. Can be a conventional date or a special type (Spring, Second Quarter, etc)."
  ; Represent as a date. Loses information, used to compare with other dates.
  (as-date [this] "Represent this as a DateTime")
  
  ; Return the 'special' type of this date
  (special-type [this] "Return the special type of this. One of [:date :year :year-month :spring :summer :autumn :winter :first-quarter :second-quarter :third-quarter :fourth-quarter]")
    
  (pp [this] "Pretty-print"))

(deftype CrossRefDate [year month day]
  ICrossRefDate
  (special-type [this] (condp = month
                            spring :spring
                            summer :summer
                            autumn :autumn
                            winter :winter
                            first-quarter :first-quarter
                            second-quarter :second-quarter
                            third-quarter :third-quarter
                            fourth-quarter :fourth-quarter
                            
                            0 :year
                            nil :year
                            
                            (condp = day
                              nil :year-month
                              0 :year-month
                              
                              ; It's only a date if all components exist.
                              :date)))
  
  (pp [this]
      (let [the-date (as-date this)
            year-str (f/unparse (f/formatter "yyyy") the-date)]
        (condp = (special-type this)
                :date (f/unparse (f/formatter "yyyy-MM-dd") the-date)
                :year-month (f/unparse (f/formatter "yyyy-MM") the-date)
                :year year-str
                :spring (str "Spring " year-str)
                :summer (str "Summer " year-str)
                :autumn (str "Autumn " year-str)
                :winter (str "Winter " year-str)
                :first-quarter (str "First quarter of " year-str)
                :second-quarter (str "Second quarter of " year-str)
                :third-quarter (str "Third quarter of " year-str)
                :fourth-quarter (str "Fourth quarter of " year-str)))) 
  
  (as-date [this]
           (let [[y m d] (condp = (special-type this)
                            :spring [year 3 1]
                            :summer [year 6 1]
                            :autumn [year 9 1]
                            :winter [year 12 1]
                            :first-quarter [year 1 1]
                            :second-quarter [year 4 1]
                            :third-quarter [year 7 1]
                            :fourth-quarter [year 10 1]
                            :year [year 1 1]
                            :year-month [year month 1]
                            :date [year month day]
                            [year month day])]
                (clj-time/date-time y m d)))
    
    ReadableInstant
    (equals  [this readableInstant] (.equals (as-date this) readableInstant))
    
    ; A competing `get` is available on the HashMap type, hence this being a deftype rather than a defrecord.
    (get [this type] (get (as-date this) type))
    (getChronology [this] (.getChronology (as-date this)))
    (getMillis [this] (.getMillis (as-date this)))
    (getZone [this] (.getZone (as-date this)))
    (hashCode [this] (.hashCode (as-date this)))
    (isAfter [this instant] (.isAfter (as-date this) instant))
    (isBefore [this instant] (.isBefore (as-date this) instant))
    (isEqual [this instant] (.isEqual (as-date this) instant))
    (isSupported [this field] (.isSupported (as-date this) field))
    (toInstant [this] (.toInstant (as-date this)))
    (toString [this] (pp this)))

(extend-protocol DateTimeProtocol
    CrossRefDate
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

(defn crossref-date
  ([y m d] (CrossRefDate. y m d))
  ([y m] (CrossRefDate. y m nil))
  ([y] (CrossRefDate. y nil nil)))

(defn parse [input]
  ; First try as a standard ISO8601 date string (without special months as that wouldn't parse).
  (let [parsed (f/parse input)]
    ; nil on error
    (if parsed
      (crossref-date (clj-time/year parsed) (clj-time/month parsed) (clj-time/day parsed)))
      (let [parts (.split input "-")
            year (Integer/parseInt (first parts))
            month (Integer/parseInt (second parts))
            day (Integer/parseInt (get parts 2))]
        (crossref-date year month day))))