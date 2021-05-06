(ns startrek.components
  (:require
   [cljsjs.semantic-ui-react]
   [goog.object]))

(set! *warn-on-infer* true)

(def semantic-ui js/semanticUIReact)

(defn ^:private component
  "Get a component from sematic-ui-react:
   (component \"Button\")
   (component \"Menu\" \"Item\")"
  [k & ks]
  (if (seq ks)
    (apply goog.object/getValueByKeys semantic-ui k ks)
    (goog.object/get semantic-ui k)))

(def s-button (component "Button"))
(def s-container (component "Container"))
(def s-grid (component "Grid"))
(def s-header (component "Header"))
(def s-icon (component "Icon"))
(def s-image (component "Image"))
(def s-label (component "Label"))
(def s-modal (component "Modal"))
(def s-modal-actions (component "Modal" "Actions"))
(def s-modal-content (component "Modal" "Content"))
(def s-modal-description (component "Modal" "Description"))
(def s-modal-header (component "Modal" "Header"))
(def s-segment (component "Segment"))
(def s-table (component "Table"))
(def s-table-body (component "Table" "Body"))
(def s-table-cell (component "Table" "Cell"))
(def s-table-header (component "Table" "Header"))
(def s-table-header-cell (component "Table" "HeaderCell"))
(def s-table-row (component "Table" "Row"))

(def s-form (component "Form"))
(def s-form-button (component "Form" "Button"))
(def s-form-field (component "Form" "Field"))
(def s-form-input (component "Form" "Input"))
(def s-form-text (component "Form" "Text"))
