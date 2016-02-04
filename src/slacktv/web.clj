(ns slacktv.web
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [environ.core]
            [hiccup.core :refer :all]
            [ring.adapter.jetty :refer :all]
            [slacktv.slack :refer [get-messages]]
            ))

(defn generate-page-for-content [content]
  (str "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
       (html
         [:html {:lang "en"}
          [:head
           [:meta {:charset "utf-8"}]
           [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
           [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
           [:link {:rel "stylesheet" :href "/css/bootstrap.min.css"}]
           [:link {:rel "stylesheet" :href "/css/business-casual.css"}]
           [:link {:rel "stylesheet" :href "http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800" :type "text/css"}]
           [:link {:rel "stylesheet" :href "http://fonts.googleapis.com/css?family=Josefin+Slab:100,300,400,600,700,100italic,300italic,400italic,600italic,700italic" :type "text/css"}]
           [:script {:src "/js/bootstrap.min.js"}]
           ]
          [:body
           (apply list content)
           ]
          ])))

(defn index [channel-name]
  (generate-page-for-content [[:div.container
                               [:div.row
                                [:div.box
                                 [:div.col-lg-12.text-center
                                  [:h1.brand-name (str "Recently on #" channel-name " ...")]]]]
                               [:div.row
                                [:div.box
                                 [:div.col-lg-12
                                  (apply concat (for [{:keys [text username]} (get-messages channel-name 5)]
                                                  [[:h3 username] [:p text]]))]]]]]))

(defn channel-name []
  (environ.core/env :slacktv-channel))

(defn app-routes []
  (apply routes [
                 (GET "/" [] (index (channel-name)))
                 (route/not-found "Not Found")
                 ]))

(defn create-app []
  (wrap-defaults (app-routes) (update-in site-defaults [:security] dissoc :frame-options)))

(defn -main []
  (let [port (if-let [port (environ.core/env :port)] port 3001)]
    (println "Starting web server on port" port)
    (run-jetty (create-app) {:port (Integer. port)})))

; This is provided for the lein ring plugin
(def app
  (create-app))
