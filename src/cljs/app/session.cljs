(ns app.session
  (:require-macros
   [cljs.core.async.macros
    :refer [go go-loop]])
  (:require
   [cljs.core.async :as async
    :refer [<!]]
   [reagent.core :as reagent]))

(defonce event-queue (async/chan))

(defonce event-mult (async/mult event-queue))

(defn dispatch [event]
  (async/put! event-queue event))

(defn dispatcher [dispatch-map]
  (let [in (async/tap event-mult (async/chan))]
    (go-loop []
      (when-let [event (<! in)]
        (when-let [f (get dispatch-map (first event))]
          (apply f (rest event)))
        (recur)))))

(defn state [initial]
  (->> initial
       (map #(vector (first %)(reagent/atom (second %))))
       (into {})))

(defn reg-event-handler [k f]
  (let [in (->> (async/chan 1 (filter #(= k (first %))))
                (async/tap event-mult))]
    (go-loop []
      (when-let [event (<! in)]
        (apply f (rest event))
        (recur)))))
