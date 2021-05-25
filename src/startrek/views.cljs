(ns startrek.views
  (:require
   [clojure.string :refer [capitalize]]
   [reagent.core :as r]
   [reitit.frontend.easy :as rfe]
   [startrek.components :as c]
   [startrek.db :as db]
   [startrek.modals :as modals]
   [startrek.the-queen :as the-queen]))

(set! *warn-on-infer* true)

(defn ^:private add-starship
  []
  (db/update-state :add-starship-modal true))

(defn ^:private talk-to-the-queen
  []
  (reset! the-queen/can-talk true)
  (rfe/push-state the-queen/talk))

(defn queen-has-finished-talking
  []
  (reset! the-queen/can-talk false)
  (rfe/push-state :startrek.main/homepage))

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

(defn ^:private render-additional-buttons
  []
  [:> c/s-container {:textAlign :right}
   [:> c/s-button {:color :blue
                   :onClick #(talk-to-the-queen)}
    "TALK TO THE QUEEN"]
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
   [render-additional-buttons]])

(defn queen-reply
  []
  [:> c/s-container {:style {:marginTop "3em"}}
   [:h1 "You will be assimilated! Resistance is Futile!"]
   [:> c/s-button {:color :green
                   :onClick #(queen-has-finished-talking)}
    "RETURN TO THE HOMEPAGE"]])

(defn go-away
  []
  [:> c/s-container {:style {:marginTop "3em"}}
   [:h1 "You aren't a borg!"]
   [:> c/s-button {:color :red
                   :onClick #(rfe/push-state :startrek.main/homepage)}
    "RETURN TO THE HOMEPAGE"]])
