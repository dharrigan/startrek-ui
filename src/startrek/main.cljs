(ns startrek.main
  (:require
   [reagent.dom :as rdom]
   [startrek.api :as api]
   [startrek.db :as db]
   [startrek.views :as views]))

(set! *warn-on-infer* true)

(defn ^:dev/after-load and-so-it-begins
  []
  (when (api/fetch-starships #(db/update-state :starships %))
    (rdom/render [views/render]
                 (.getElementById js/document "root"))))

(defn init
  []
  (and-so-it-begins))
