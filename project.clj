(defproject om-charts "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  :min-lein-version "2.3.4"

  :source-paths ["src/clj" "src/cljs"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2227"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.6.3"]
                 [net.drib/strokes "0.5.1"]]
  
  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-ancient "0.5.5"]]

  :hooks [leiningen.cljsbuild]

  :repl-options {
                  ;; If nREPL takes too long to load it may timeout,
                  ;; increase this to wait longer before timing out.
                  ;; Defaults to 30000 (30 seconds)
                  :timeout 60000
                  }

  :cljsbuild
  {:builds {:om-charts
            {:source-paths ["src/cljs"]
             :compiler
             {:output-to "dev-resources/public/js/om_charts.js"
              :optimizations :advanced
              :pretty-print false}}}})
