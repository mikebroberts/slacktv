(ns slacktv.slack
  (:require
    [environ.core]
    [clj-http.client :as client]
    [cheshire.core :as json]
    [clojure.core.memoize :refer [ttl]]
    [hiccup.util :refer [escape-html]]
    ))

(defn token []
  (environ.core/env :slack-key))

(def api-base-url "https://slack.com/api/")

(defn get-slack [request & [params-string]]
  ; Uncomment for debugging requests to Slack
  ;(println request params-string)
  (->
    (client/get
      (str api-base-url request "?token=" (token) (when params-string (str "&" params-string))))
    :body
    (json/parse-string true)))

(defn get-channel-id-uncached [channel-name]
  (->>
    (get-slack "channels.list")
    :channels
    (filter #(= channel-name (:name %)))
    first
    :id
    ))

; Cache channel ID lookup for 10 minutes
(def get-channel-id (ttl get-channel-id-uncached {} :ttl/threshold 600000))

(defn get-user-name-uncached [user-id]
  (->> (get-slack "users.info" (str "user=" user-id))
       :user :real_name))

; Cache username lookups for 10 minutes
(def get-user-name (ttl get-user-name-uncached {} :ttl/threshold 600000))

(defn replace-user-tag [user-tag]
  (let [bar-index (.indexOf user-tag "|")
        user-id (subs user-tag 2 (if (> bar-index 0) bar-index (- (.length user-tag) 1)))]
    (get-user-name user-id)))

(defn slack-expand [text]
  (escape-html
    (try
      (clojure.string/replace text #"<@U\S+>" #(replace-user-tag %))
      (catch Throwable t (println "Unable to expand: " text) (.printStackTrace t))
      (finally text)
      )))

(defn get-messages-uncached [channel-name channel-count]
  (->>
    (get-slack "channels.history" (str "channel=" (get-channel-id channel-name) "&count=" channel-count))
    :messages
    (map (fn [{user-id :user text :text :as message}]
           (assoc message
             :username (get-user-name user-id)
             :text (slack-expand text)
             )))
    (filter (comp not #(clojure.string/blank? (:text %))))
    reverse))

; Cache getting messages for 10 seconds
(def get-messages (ttl get-messages-uncached {} :ttl/threshold 10000))
