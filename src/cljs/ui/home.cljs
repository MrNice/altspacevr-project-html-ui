(ns ui.home
  (:require [reagent.core :refer [atom]]
            [clojure.string :as s]
            [re-com.core :refer [h-box v-box box gap md-circle-icon-button]]
            [ui.model :as md :refer [app-state]]
            [ui.edit   :refer [delete edit-page]]
            [ui.common :refer [container]]))

;; Actions
(defn edit [space]
  (let [url (str "#/edit/" (:id space))]
    (md/update-space! (conj space {:editing true}))
    (js/window.location.assign url)))

(defn create []
  (md/add-space! (md/make-space)))

;; View Helpers
(defn format-text [string]
  [:div.space-text
    (for [para (s/split string "\n")]
    ^{:key para} [:p para])])

(defn note-weight [space]
  "Control how spaces are sorted, based on type
   Welcome > Featured > Else"
  (let [{:keys [type]} space]
    (cond (= "starred" type) 0
          :else 2)))

(def sort-notes (partial sort-by note-weight))

;; Views
(defn note-title [type title]
  [:span {:class (str "title " type)} [:span.name title]])

(defn note [space]
  (let [{:keys [id title creator text members type]} space]
    [:div
      [h-box :justify :between :children [
        [note-title type title creator]
        [:div.buttons
          [md-circle-icon-button
            :md-icon-name "md-delete"
            :on-click #(delete space)
            :class "abtn delete"]
          [md-circle-icon-button
            :md-icon-name "md-edit"
            :on-click #(edit space)
            :class "abtn edit"]]]]
      [format-text text]]))

(defn note-card [space]
  "Determines whether or not to render current note as and edit pane"
  [v-box :class (str "space " type) :align-self :stretch :children [
    (if (:editing space)
      [edit-page space]
      [note space])]])

(defn home-page []
  [container "home-page"
    [:h1.page-title "notes:"]
    (interpose [gap :size "10px"] (map note-card (sort-notes (:spaces @app-state))))
    [gap :size "10px"]
    [h-box :align-self :center :children [
      [:div.create-tab
        [md-circle-icon-button :md-icon-name "md-add" :size :larger :on-click create :class "abtn create"]]]]])
