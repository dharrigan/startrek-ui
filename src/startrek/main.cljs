(ns startrek.main
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reitit.coercion.spec :as rss]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]
   [startrek.api :as api]
   [startrek.db :as db]
   [startrek.the-queen :as the-queen]
   [startrek.views :as views]))

(set! *warn-on-infer* true)

(def state (r/atom nil))

(def routes
  [["/" {:name ::homepage :view views/render}]
   ["/the-queen" {:name the-queen/talk :view views/queen-reply}]])

(def router
  (rf/router routes {:data {:coercion rss/coercion}}))

(defn on-navigate
  [new-match]
  (reset! state new-match))

(defn landing-page
  []
  (let [{{:keys [view name]} :data} @state]
    (if (= :startrek.the-queen/talk name)
      (if @the-queen/can-talk
        (view @state)
        [views/go-away])
      (view @state))))

(defn ^:dev/after-load and-so-it-begins
  []
  (when (api/fetch-starships #(db/update-state :starships %))
    (rfe/start! router on-navigate {:use-fragment true})
    (rdom/render [landing-page]
                 (.getElementById js/document "root"))))

(defn init
  []
  (and-so-it-begins))
