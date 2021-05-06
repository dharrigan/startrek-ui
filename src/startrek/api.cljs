(ns startrek.api
  (:require
   [ajax.core :refer [DELETE GET PATCH POST]]))

(goog-define BASE-URL "")

(def ^:private url (str BASE-URL "/api/starships"))

(defn add-starship
  [starship callback]
  (POST url {:params starship
             :handler callback
             :error-handler #(println %)}))

(defn delete-starship
  [id callback]
  (DELETE (str url "/" id) {:handler callback}))

(defn edit-starship
  [starship callback]
  (PATCH (str url "/" (:id starship)) {:params starship
                                       :handler callback
                                       :error-handler #(println %)}))

(defn fetch-starships
  [callback]
  (GET url {:handler callback}))
