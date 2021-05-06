(ns startrek.db
  (:require
   [reagent.core :as r]))

(def ^:private app-state (r/atom {:add-starship-modal nil
                                  :delete-starship-modal nil
                                  :edit-starship-modal nil
                                  :starship nil
                                  :starships nil
                                  :view-starship-modal nil}))

(defn get-state
  [k]
  (get @app-state k))

(defn update-state
  [k v]
  (swap! app-state assoc k v))

(defn remove-state
  [k]
  (swap! app-state dissoc k))
