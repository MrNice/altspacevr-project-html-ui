(ns ui.common
    (:require [re-com.core :refer [h-box v-box box gap]])
    (:import goog.History))

(defn container [class & args]
  "Contains the entire page, enforcing size and centering"
  [h-box
    :justify :center
    :class class
    :children
      [[v-box
        :size "1"
        :class "container"
        :children [args]]]])
