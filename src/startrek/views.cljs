(ns startrek.views
  (:require
   [clojure.string :refer [capitalize]]
   [reagent.core :as r]
   [startrek.components :as c]
   [startrek.db :as db]
   [startrek.modals :as modals]))

(set! *warn-on-infer* true)

(defn ^:private add-starship
  []
  (db/update-state :add-starship-modal true))

(defn ^:private delete-starship
  [starship]
  (db/update-state :starship starship)
  (db/update-state :delete-starship-modal true))

(defn ^:private edit-starship
  [starship]
  (db/update-state :starship starship)
  (db/update-state :edit-starship-modal true))

(defn ^:private view-starship
  [starship]
  (db/update-state :starship starship)
  (db/update-state :view-starship-modal true))

(defn ^:private perform-sort
  [sorting k direction compare-fn]
  (->> (db/get-state :starships)
       (sort-by k compare-fn)
       (db/update-state :starships))
  (swap! sorting assoc k direction))

(defn ^:private flip-sorting
  [sorting k]
  (if (= (get @sorting k) :ascending)
    (perform-sort sorting k :descending #(compare %2 %1))
    (perform-sort sorting k :ascending #(compare %1 %2))))

(defn ^:private table-header-defaults
  [sorting n]
  [:> c/s-table-header-cell {:sorted (-> @sorting n)
                             :onClick #(flip-sorting sorting n)}
   (capitalize (name n))])

(defn ^:private render-starships
  []
  (let [sorting (r/atom {:registry :ascending
                         :captain :ascending
                         :class :ascending
                         :launched :ascending
                         :affiliation :ascending})]
    (fn []
      [:> c/s-table {:selectable true
                     :sortable true}
       [:> c/s-table-header
        [:> c/s-table-row
         (table-header-defaults sorting :registry)
         (table-header-defaults sorting :captain)
         (table-header-defaults sorting :class)
         (table-header-defaults sorting :launched)
         (table-header-defaults sorting :affiliation)
         [:> c/s-table-header-cell "Actions"]]]
       [:> c/s-table-body
        (map (fn [{:keys [id registry captain class launched affiliation] :as starship}]
               [:> c/s-table-row {:key id}
                [:> c/s-table-cell registry]
                [:> c/s-table-cell captain]
                [:> c/s-table-cell class]
                [:> c/s-table-cell launched]
                [:> c/s-table-cell affiliation]
                [:> c/s-table-cell
                 [:> c/s-icon {:name :search
                               :color :blue
                               :onClick #(view-starship starship)}]
                 [:> c/s-icon {:name :pencil
                               :color :brown
                               :onClick #(edit-starship starship)}]
                 [:> c/s-icon {:name :trash
                               :color :red
                               :onClick #(delete-starship starship)}]]]) (db/get-state :starships))]])))

(defn ^:private render-add-starship-button
  []
  [:> c/s-container {:textAlign :right}
   [:> c/s-button {:color :green
                   :onClick #(add-starship)}
    "ADD STARSHIP"]])

(defn render
  []
  [:> c/s-container {:style {:marginTop "3em"}}
   [render-starships]
   (cond
    (db/get-state :add-starship-modal) [modals/add-starship]
    (db/get-state :delete-starship-modal) [modals/delete-starship]
    (db/get-state :edit-starship-modal) [modals/edit-starship (db/get-state :starship)]
    (db/get-state :view-starship-modal) [modals/view-starship])
   [render-add-starship-button]])
