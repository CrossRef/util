(defproject crossref-util "0.1.13"
  :description "CrossRef Utils"
  :url "http://github.com/crossref/util"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.8.0"]
                 [org.clojure/tools.logging "0.3.0"]
                 [clj-http "1.0.0"]
                 [http-kit "2.1.16"]
                 [org.clojure/data.json "0.2.4"]]
  
  :repl-options {:init-ns crossref.util.doi}
  :main nil)
