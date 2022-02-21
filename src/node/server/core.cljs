(ns server.core
  (:require-macros
   [cljs.core.async.macros :as m
    :refer [go go-loop alt!]])
  (:require
   [polyfill.compat]
   [cljs.nodejs :as nodejs]
   [cljs.core.async :as async
    :refer [chan close! timeout put!]]
   [reagent.core :as reagent
    :refer [atom]]
   [api.jokes :as jokes]
   [app.core :as app
    :refer [static-page]]))

(enable-console-print!)

(def express (nodejs/require "express"))

(defn handler [req res]
  (if (= "https" (aget (.-headers req) "x-forwarded-proto"))
    (.redirect res (str "http://" (.get req "Host") (.-url req)))
    (go
      (.set res "Content-Type" "text/html")
      (.send res (<! (static-page))))))

(defn api-handler [req res]
  (go-loop [in (jokes/resource-chan)
            [val ch] (alts! [in (timeout 5000)])]
    (if (identical? in ch)
      (do
        (.set res "Content-Type" "application/json")
        (.send res (clj->js val)))
      (do
        (.send (.status res 504) "Gateway Timeout")))))

(defn server [port success]
  (doto (express)
    (.get "/" handler)
    (.get "/api" api-handler)
    (.use (.static express "resources/public"))
    (.listen port success)))

(defn -main [& mess]
  (let [port (or (.-PORT (.-env js/process)) 1337)]
    (server port
            #(println (str "Server running at http://127.0.0.1:" port "/")))))

(set! *main-cli-fn* -main)
