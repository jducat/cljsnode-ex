(ns api.jokes
  (:require-macros
   [cljs.core.async.macros
    :refer [go go-loop alt!]])
  (:require
   [goog.string :as gstring]
   [cljs.core.async :as async
    :refer [chan close! timeout put!]]
   [cljs-http.client :as http]))

(defn json-onto [url ch]
  (http/get url {:channel ch :with-credentials? false}))

(defn open-resource
  ([endpoint n]
   (open-resource endpoint n 1))
  ([{:keys [url extract] :as endpoint} n buf & {:keys [concur] :or {concur n}}]
   {:pre [(string? url) (fn? extract)(int? n)]}
   (let [out (chan buf (comp
                         (map extract)
                         (partition-all n)))
         in (chan n)]
     ;; Preferable but cannot do yet due to bug in core.async:
     ;; http://dev.clojure.org/jira/browse/ASYNC-108
     ;; (async/to-chan (repeat url))
     (async/onto-chan in (repeat url))
     (async/pipeline-async concur out json-onto in)
     out)))

(def endpoint {:url "http://api.icndb.com/jokes/random"
               :extract (fn [{:keys [success body error-code error-text]
                              :as response}]
                          (if-let [joke (get-in body [:value :joke])]
                            (gstring/unescapeEntities joke)
                            ""))})

(defonce resource-chan
  (memoize #(open-resource endpoint 12 2)))
