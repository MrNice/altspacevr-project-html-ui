(ns ui.edit
  (:require [reagent.core :refer [atom]]
            [clojure.string :as s]
            [re-com.core :refer [h-box v-box box gap selection-list md-circle-icon-button]]
            [re-com.misc :refer [input-text input-textarea radio-button]]
            [ui.model :as md :refer [app-state]]
            [ui.common :refer [container]]))

;; Action Helpers
(defn set-space-value! [key value space]
  "Sets a key on a space, used by the on-change events"
  (md/update-space! (conj space {key value})))

;; Actions
(defn save [space]
  "Also known as update-or-add"
  (if (> (:id space) (apply max (map :id (:spaces @app-state))))
    (md/add-space! space)
    (md/update-space! (conj space {:editing false})))
  (js/window.location.assign "#/"))

(defn delete [space]
  "Remove a space, by its ID"
  (md/remove-space! (:id space))
  (js/window.location.assign "#/"))

;; EDIT PAGE
(defn edit-line [space label value key]
  [h-box :children [
    [box :size "6" :child
      [input-text :model value
                  :class "editor"
                  :on-change #(set-space-value! key % space)]]]])

(defn edit-box
  [space label value key rows & [sanitizer]]
    [h-box :children [
      [box :size "6" :child
        [input-textarea :model value :rows (if rows rows 3) :class "editor"
          :on-change #(set-space-value! key ((or sanitizer identity) %) space)]]]])

(defn space-type [space value t]
  (radio-button :model t :label (s/capitalize t) :value value :on-change #(set-space-value! :type t space)))

(defn space-type-selector [space value]
  [h-box :children [
    [v-box :size "6" :children
      (map (partial space-type space value) ["public" "starred" "private"])]]])

(defn edit-page [note]
  (let [space note]
    (let [{:keys [id title creator text members type]} space]
      [container "edit-page"
       (interpose [gap :size "10px"] [
        [:h4 "Title"]
        [edit-line space "Title" title :title]
        [:h4 "type"]
        [space-type-selector space type]
        [edit-box space "Content" text :text]
          [h-box :class "edit-delete-cancel-save" :justify :between :children [
            [:div.delete
              (if-not (:new space)
                [md-circle-icon-button :md-icon-name "md-delete" :size :larger :on-click #(delete space) :class "abtn delete"])]
            [:div.aligner
              [:div.cancel-save
                [md-circle-icon-button :md-icon-name "md-save"   :size :larger :on-click #(save space) :class "abtn save"]]]]]])])))
