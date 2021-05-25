(ns startrek.the-queen
  (:require
   [reagent.core :as r]))

(def can-talk (r/atom false))

(def talk ::talk)
