(ns ui.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [clojure.string :as string]
              [re-com.core :refer [h-box v-box box gap]]
              [re-com.buttons :refer [button]]
              [re-com.misc :refer [input-text input-textarea radio-button]]
              [ui.model :refer [app-state]]
              [ui.common :refer [container]]
              [ui.edit :refer [edit-page delete]])
    (:import goog.History))

;; Main Page
;; Actions
(defn edit [index]
  (let [url (str "#/edit/" index)]
    (js/window.location.assign url)))

(defn add-space! [space]
  (swap! app-state assoc-in [:spaces] (conj (:spaces @app-state) space)))

(defn create-space! []
  (js/window.location.assign "#/create"))

;; -------------------------
;; View Helpers
(defn format-text [strng]
  [:div.space-text
    (for [para (string/split strng "\n")]
    ^{:key para} [:p para])])

;; Views

(defn space-title [title creator]
  [:span.title [:span.name title] [:span (str "created by " creator)]])

(defn space-component [index {:keys [title creator text]} space]
  [v-box :class "space" :align-self :stretch :children [
    [space-title title creator]
    [format-text text]
    [h-box :children [
      [button
        :label "Edit"
        :tooltip "Change this space"
        :tooltip-position :above-center
        :on-click #(edit index)
        :class "btn-default edit"]
      [button
        :label "Delete"
        :tooltip "Delete this space"
        :tooltip-position :above-center
        :on-click #(delete index)
        :class "btn-danger edit"]]
        :justify :end]]])

(defn home-page []
  [container "home-page"
    [:h1 "Altspace Spaces Admin"]
    (interpose [gap :size "10px"] (map-indexed space-component (:spaces @app-state)))
    [gap :size "10px"]
    [h-box
      :align-self :end
      :children
        [[button :label "Create" :on-click create-space!]]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/edit/:id" {:as params}
  (session/put! :current-space (:id params))
  (session/put! :current-page #'edit-page))

(secretary/defroute "/create" []
  ;; use -1 because nil is cast to 0
  (session/put! :current-space -1)
  (session/put! :current-page #'edit-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
