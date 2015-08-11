(ns ui.edit
  (:require [reagent.core :refer [atom]]
            [reagent.session :as session]
            [clojure.string :as s]
            [re-com.core :refer [h-box v-box box gap selection-list md-circle-icon-button]]
            [re-com.misc :refer [input-text input-textarea radio-button]]
            [ui.model :as md :refer [app-state]]
            [ui.common :refer [container]]))

;; Local State
(defonce current (atom {:title "" :creator (session/get :current-user) :text "" :members [] :type "standard"}))
(defonce current-members (atom #{}))

(defn reset-local-space! [space]
  "Reset local state atoms with the new space"
  (reset! current space)
  (reset! current-members (set (:members space))))

;; Action Helpers
(defn set-space-value! [key value]
  "Sets a key on the current space, used by the on-change events"
  (swap! current assoc-in [key] value))

;; Actions
(defn save [id]
  "Also known as update or add"
  (swap! current #(assoc % :members @current-members))
  (let [space @current]
    (if (> id (apply max (map :id (:spaces @app-state))))
      (md/add-space! space)
      (md/update-space! space)))
  (js/window.location.assign "#/"))

(defn cancel []
  "Leave the page, all temp edits will be overwritten
   when the edit page is rerendered"
  (let [url (str "#/")]
    (js/window.location.assign url)))

(defn delete [id]
  "Remove a space, by its ID"
  (md/remove-space! id)
  (js/window.location.assign "#/"))

;; EDIT PAGE
(defn edit-line [label value key]
  [h-box :children [
    [box :justify :end :size "1" :child [:span.descriptor label]]
    [box :size "6" :child
      [input-text :model value
                  :class "editor"
                  :on-change #(set-space-value! key %)]]]])

(defn member-selector [label]
  [h-box :children [
    [box :justify :end :size "1" :child [:span.descriptor label]]
    [box :size "6" :class "member-selector"  :child
      [selection-list :model current-members
                      :choices (filter #(not= 0 (:id %)) (:members @app-state))
                      :label-fn #(:name %)
                      ;; NOTE: Keep 600px in alignment with cancel-save
                      :width "600px"
                      :hide-border? true
                      :on-change #(reset! current-members %)]]]])

(defn edit-box
  [label value key rows & [sanitizer]]
    (h-box :children [
      [box :justify :end :size "1" :child [:span.descriptor label]]
      [box :size "6" :child
        [input-textarea :model value :rows (if rows rows 3) :class "editor"
          :on-change #/conso(set-space-value! key ((or sanitizer identity) %))]]]))

(defn space-type [value t]
  (radio-button :model t :label (s/capitalize t) :value value :on-change #(set-space-value! :type t)))

(defn space-type-selector []
  (let [value (:type @current)]
    [h-box :children
     [[box   :size "1" :child [:div]]
      [v-box :size "6" :children
        (interpose [gap :size "15px"]
          (map (partial space-type value) ["welcome" "featured" "standard" "private"]))]]]))

(defn edit-page []
  (let [creating (session/get :creating)
        space (if creating (md/make-space) (session/get :current-space))]
    (reset-local-space! space)
    (let [{:keys [id title creator text members type]} space]
      [container "edit-page"
       (interpose [gap :size "20px"] [
        [:h1 (str "Altspace Spaces Admin - " (if-not (= title "") title "New Space"))]
        [edit-line "Title" title :title]
        [edit-box "Description" text :text]
        [space-type-selector type]
        [member-selector "Members"]
        (if (md/auth (session/get :current-user))
          [h-box :class "edit-delete-cancel-save" :justify :between :children [
            [:div.delete
              (if-not creating
                [md-circle-icon-button :md-icon-name "md-delete" :size :larger :on-click #(delete id) :class "delete"])]
            [:div.aligner
              [:div.cancel-save
                [md-circle-icon-button :md-icon-name "md-cancel" :size :larger :on-click cancel     :class "cancel"]
                [md-circle-icon-button :md-icon-name "md-save"   :size :larger :on-click #(save id) :class "save"]]]]])])])))
