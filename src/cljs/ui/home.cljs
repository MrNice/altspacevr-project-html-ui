(ns ui.home
  (:require [reagent.core :refer [atom]]
            [clojure.string :as s]
            [re-com.core    :refer [h-box v-box box gap md-circle-icon-button]]
            [ui.model :as md :refer [app-state]]
            [ui.common :refer [container]]
            [ui.edit   :refer [delete]]))

;; Actions
(defn edit [id]
  (let [url (str "#/edit/" id)]
    (js/window.location.assign url)))

(defn create []
  (js/window.location.assign "#/create"))

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

(defn note [{:keys [id title creator text members type]} space]
  [v-box :class (str "space " type) :align-self :stretch :children [
    [note-title type title creator]
    [format-text text]
    [:div
      [md-circle-icon-button
        :md-icon-name "md-delete"
        :on-click #(delete id)
        :class "abtn delete"]
      [md-circle-icon-button
        :md-icon-name "md-edit"
        :on-click #(edit id)
        :class "abtn edit"
        :size :larger]]]])

(defn home-page []
  [container "home-page"
    [:h1 "Notes"]
    (interpose [gap :size "10px"] (map note (sort-notes (:spaces @app-state))))
    [gap :size "10px"]
    [h-box :align-self :center :children [
      [md-circle-icon-button :md-icon-name "md-add" :size :larger :on-click create :class "create"]]]])
