(ns ui.home
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core  :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [clojure.string :as s]
            [re-com.core    :refer [h-box v-box box gap md-circle-icon-button]]
            [re-com.buttons :refer [button]]
            [ui.model  :refer [app-state add-space! get-member]]
            [ui.common :refer [container]]
            [ui.edit   :refer [delete]])
  (:import goog.History))

;; Actions
(defn edit [id]
  (js/console.log "This is the ID that edit sees inside home" id)
  (let [url (str "#/edit/" id)]
    (js/window.location.assign url)))

(defn create []
  (js/window.location.assign "#/create"))

;; -------------------------
;; View Helpers
(defn format-text [string]
  [:div.space-text
    (for [para (s/split string "\n")]
    ^{:key para} [:p para])])

(defn space-weight [space]
  (let [{:keys [type]} space]
    (cond (= "welcome" type) 0
          (= "featured" type) 1
          :else 2)))

(def sort-spaces (partial sort-by space-weight))

;; Views & Sub-views
(defn member-icon [id]
  (let [member (get-member id)]
    [:img {:alt name
           :src (str "http://eightbitavatar.herokuapp.com/?id=" (:name member) "&s=" (:gender member) "&size=48")}]))

(defn space-title [type title creatorid]
  (let [creator (:name (get-member creatorid))]
    [:span {:class (str "title " type)} [:span.name title] [:span (str "created by " creator)]]))

(defn space-component [{:keys [id title creator text members type]} space]
  [v-box :class (str "space " type) :align-self :stretch :children [
    (js/console.log id creator)
    [space-title type title creator]
    [format-text text]
    [h-box :justify :between :children [
      [:div.icons [:span "Current Members: "] (map member-icon members)]
      [:div
        [md-circle-icon-button
          :md-icon-name "md-delete"
          :on-click #(delete id)
          :class "abtn delete"]
        [md-circle-icon-button
          :md-icon-name "md-edit"
          :size :larger
          :on-click #(edit id)
          :class "abtn edit"]]]]]])

(defn home-page []
  [container "home-page"
    [:h1 "Altspace Spaces Admin"]
    (interpose [gap :size "10px"] (map space-component (sort-spaces (:spaces @app-state))))
    [gap :size "10px"]
    [h-box :align-self :end :children [
      [md-circle-icon-button :md-icon-name "md-add" :size :larger :on-click create :class "create"]]]])
