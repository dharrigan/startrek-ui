(ns startrek.validation
  (:require
   [clojure.string :refer [capitalize]]))

(defn ^:private validate-field
  [field-name value min max]
  (if (string? value)
    (when (or (< (count value) min) (> (count value) max))
      (str (capitalize (name field-name)) " must be between " min " and " max " in length"))
    (when (or (< value min) (> value max))
      (str (capitalize (name field-name)) " must be between " min " and " max))))

(defn validate
  [data field-name]
  (let [{{:keys [min max]} :validation :keys [value]} (get data field-name)]
    (validate-field field-name value min max)))
