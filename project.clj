(defproject slacktv "0.1.0-SNAPSHOT"
  :description "Slack TV"
  :url "http://example.com/FIXME"
  :dependencies [
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/core.memoize "0.5.8"]
                 [environ "1.0.1"]
                 [clj-http "2.0.0"]
                 [cheshire "5.5.0"]
                 [clj-time "0.11.0"]
                 [compojure "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [hiccup "1.0.5"]
                 ]
  :plugins [
            [lein-environ "1.0.1"]
            [lein-ring "0.9.6"]
            ]

  :main slacktv.web
  :ring {:handler slacktv.web/app}
  :uberjar-name "slacktv-standalone.jar"
)