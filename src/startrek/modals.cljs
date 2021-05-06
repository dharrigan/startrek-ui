(ns startrek.modals
  (:require
   [reagent.core :as r]
   [startrek.api :as api]
   [startrek.components :as c]
   [startrek.db :as db]
   [startrek.validation :as v]))

(set! *warn-on-infer* true)

(defn ^:private on-starship-added
  [_]
  (api/fetch-starships #(db/update-state :starships %))
  (db/update-state :add-starship-modal false))

(defn ^:private on-add-starship
  [event data]
  (.preventDefault event)
  (let [starship {:registry (-> data :registry :value)
                  :captain (-> data :captain :value)
                  :class (-> data :class :value)
                  :launched (-> data :launched :value)
                  :affiliation (-> data :affiliation :value)}]
    (api/add-starship starship on-starship-added)))

(defn add-starship
  []
  (let [data (r/atom {:registry {:value "" :validation {:min 1 :max 15}}
                      :captain {:value "" :validation {:min 1 :max 20}}
                      :class {:value "" :validation {:min 1 :max 15}}
                      :launched {:value 2000 :validation {:min 2000 :max 3000}}
                      :affiliation {:value "" :validation {:min 1 :max 30}}})]
    (fn []
      [:> c/s-modal {:open (db/get-state :add-starship-modal)
                     :onClose #(db/update-state :add-starship-modal false)
                     :size :small}
       [:> c/s-modal-header "Add Starship"]
       [:> c/s-modal-content
        [:> c/s-form
         [:> c/s-form-field
          {:required true}
          [:label "Registry"]
          [:> c/s-form-input {:name :registry
                              :value (-> @data :registry :value)
                              :placeholder "Registry"
                              :error (v/validate @data :registry)
                              :onChange #(swap! data assoc-in [:registry :value] (-> % .-target .-value))}]]
         [:> c/s-form-field
          {:required true}
          [:label "Captain"]
          [:> c/s-form-input {:name :captain
                              :value (-> @data :captain :value)
                              :placeholder "Captain"
                              :error (v/validate @data :captain)
                              :onChange #(swap! data assoc-in [:captain :value] (-> % .-target .-value))}]]
         [:> c/s-form-field
          {:required true}
          [:label "Class"]
          [:> c/s-form-input {:name :class
                              :value (-> @data :class :value)
                              :placeholder "Class"
                              :error (v/validate @data :class)
                              :onChange #(swap! data assoc-in [:class :value] (-> % .-target .-value))}]]
         [:> c/s-form-field
          {:required true}
          [:label "Launched"]
          [:> c/s-form-input {:name :launched
                              :value (-> @data :launched :value)
                              :placeholder "Launched"
                              :type :number
                              :error (v/validate @data :launched)
                              :onChange #(swap! data assoc-in [:launched :value] (js/parseInt (-> % .-target .-value)))}]]
         [:> c/s-form-field
          {:required true}
          [:label "Affiliation"]
          [:> c/s-form-input {:name :affiliation
                              :value (-> @data :affiliation :value)
                              :placeholder "Affiliation"
                              :error (v/validate @data :affiliation)
                              :onChange #(swap! data assoc-in [:affiliation :value] (-> % .-target .-value))}]]]]
       [:> c/s-modal-actions
        [:> c/s-button {:color :green
                        :onClick #(on-add-starship % @data)} "Submit"]
        [:> c/s-button {:color :red
                        :onClick #(db/update-state :add-starship-modal false)} "Cancel"]]])))

(defn ^:private on-starship-deleted
  [_]
  (db/update-state :delete-starship-modal false)
  (api/fetch-starships #(db/update-state :starships %)))

(defn ^:private on-delete-starship
  [id]
  (api/delete-starship id on-starship-deleted))

(defn delete-starship
  []
  (let [{:keys [id registry affiliation captain launched image class]} (db/get-state :starship)]
    [:> c/s-modal {:open (db/get-state :delete-starship-modal)
                   :onClose #(db/update-state :delete-starship-modal false)
                   :size :small}
     [:> c/s-modal-header (str "Delete Starship " registry "?")]
     [:> c/s-modal-content {:image true}
      [:> c/s-image {:size :medium
                     :src (str "images/" (or image "borg.png"))}]
      [:> c/s-modal-description (str "Registry: " registry)
       [:p]
       [:p (str "Affiliation: " affiliation)]
       [:p (str "Captain: " captain)]
       [:p (str "Class: " class)]
       [:p (str "Launched: " launched)]]]
     [:> c/s-modal-actions
      [:> c/s-button {:color :green
                      :onClick #(on-delete-starship id)}
       "OK"]
      [:> c/s-button {:color :red
                      :onClick #(db/update-state :delete-starship-modal false)}
       "CANCEL"]]]))

(defn ^:private on-starship-edited
  [_]
  (api/fetch-starships #(db/update-state :starships %))
  (db/update-state :edit-starship-modal false))

(defn ^:private on-edit-starship
  [event data]
  (.preventDefault event)
  (let [starship {:id (-> data :id :value)
                  :registry (-> data :registry :value)
                  :captain (-> data :captain :value)
                  :class (-> data :class :value)
                  :launched (-> data :launched :value)
                  :affiliation (-> data :affiliation :value)}]
    (api/edit-starship starship on-starship-edited)))

(defn edit-starship
  [{:keys [id registry captain class launched affiliation]}]
  (let [data (r/atom {:id {:value id}
                      :registry {:value registry :validation {:min 1 :max 15}}
                      :captain {:value captain :validation {:min 1 :max 20}}
                      :class {:value class :validation {:min 1 :max 15}}
                      :launched {:value launched :validation {:min 2000 :max 3000}}
                      :affiliation {:value affiliation :validation {:min 1 :max 30}}})]
    (fn []
      [:> c/s-modal {:open (db/get-state :edit-starship-modal)
                     :onClose #(db/update-state :edit-starship-modal false)
                     :size :small}
       [:> c/s-modal-header (str "Edit Starship " registry)]
       [:> c/s-modal-content
        [:> c/s-form
         [:> c/s-form-field
          {:required true}
          [:label "Registry"]
          [:> c/s-form-input {:name :registry
                              :value (-> @data :registry :value)
                              :placeholder "Registry"
                              :error (v/validate @data :registry)
                              :onChange #(swap! data assoc-in [:registry :value] (-> % .-target .-value))}]]
         [:> c/s-form-field
          {:required true}
          [:label "Captain"]
          [:> c/s-form-input {:name :captain
                              :value (-> @data :captain :value)
                              :placeholder "Captain"
                              :error (v/validate @data :captain)
                              :onChange #(swap! data assoc-in [:captain :value] (-> % .-target .-value))}]]
         [:> c/s-form-field
          {:required true}
          [:label "Class"]
          [:> c/s-form-input {:name :class
                              :value (-> @data :class :value)
                              :placeholder "Class"
                              :error (v/validate @data :class)
                              :onChange #(swap! data assoc-in [:class :value] (-> % .-target .-value))}]]
         [:> c/s-form-field
          {:required true}
          [:label "Launched"]
          [:> c/s-form-input {:name :launched
                              :value (-> @data :launched :value)
                              :placeholder "Launched"
                              :type :number
                              :error (v/validate @data :launched)
                              :onChange #(swap! data assoc-in [:launched :value] (js/parseInt (-> % .-target .-value)))}]]
         [:> c/s-form-field
          {:required true}
          [:label "Affiliation"]
          [:> c/s-form-input {:name :affiliation
                              :value (-> @data :affiliation :value)
                              :placeholder "Affiliation"
                              :error (v/validate @data :affiliation)
                              :onChange #(swap! data assoc-in [:affiliation :value] (-> % .-target .-value))}]]]]
       [:> c/s-modal-actions
        [:> c/s-button {:color :green
                        :onClick #(on-edit-starship % @data)} "Submit"]
        [:> c/s-button {:color :red
                        :onClick #(db/update-state :edit-starship-modal false)} "Cancel"]]])))

(defn view-starship
  []
  (let [{:keys [registry affiliation captain launched image class]} (db/get-state :starship)]
    [:> c/s-modal {:open (db/get-state :view-starship-modal)
                   :onClose #(db/update-state :view-starship-modal false)
                   :size :small}
     [:> c/s-modal-header (str "View Starship " registry)]
     [:> c/s-modal-content {:image true}
      [:> c/s-image {:size :medium
                     :src (str "images/" (or image "borg.png"))}]
      [:> c/s-modal-description (str "Registry: " registry)
       [:p]
       [:p (str "Affiliation: " affiliation)]
       [:p (str "Captain: " captain)]
       [:p (str "Class: " class)]
       [:p (str "Launched: " launched)]]]
     [:> c/s-modal-actions
      [:> c/s-button {:color :green
                      :onClick #(db/update-state :view-starship-modal false)}
       "OK"]]]))
