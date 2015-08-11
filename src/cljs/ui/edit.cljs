(ns ui.edit
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [clojure.string :as s]
            [re-com.core :refer [h-box v-box box gap selection-list md-circle-icon-button]]
            [re-com.buttons :refer [button]]
            [re-com.misc :refer [input-text input-textarea radio-button]]
            [ui.model :refer [app-state add-space! remove-space! get-member]]
            [ui.common :refer [container]])
  (:import goog.History))

;; Because this state is local, do not store within model.cljs
(defonce current (atom {:title "" :creator (session/get :current-user) :text "" :members [] :type "standard"}))
(defonce current-members (atom #{}))

(defn positions
  [pred coll]
  (keep-indexed (fn [idx x]
                  (when (pred x)
                    idx))
                coll))

;; Action Helpers
(defn update-space! []
  "Saves the current temp space into the DB atom
   Beware of this tight coupling, uses current index
   as well as the ID. Consider turning :spaces into a
   map instead of a vector"
  (let [index (first (positions #(= (:id @current) (:id %)) (:spaces @app-state)))]
    (swap! app-state update-in [:spaces index] #(merge % @current))))

(defn set-space-value! [key value]
  "Sets a key on the current space, used by the on-change events"
  (swap! current assoc-in [key] value))

;; Actions
(defn save [id]
  "Also known as update or add"
  (swap! current #(assoc % :members @current-members))
  (if (> id (apply max (map :id (:spaces @app-state))))
    (add-space! @current)
    (update-space!))
  (js/window.location.assign "#/"))

(defn cancel []
  "Leave the page, all temp edits will be overwritten
   when the edit page is rerendered"
  (let [url (str "#/")]
    (js/window.location.assign url)))

(defn delete [id]
  "Remove a space, by its ID"
  (remove-space! id)
  (js/window.location.assign "#/"))

;; EDIT PAGE
;; NOTE: Keep 600px in alignment with cancel-save
(defn edit-line [label value key]
  "An input-text with a label"
  [h-box :children [
    [box :justify :end :size "1" :child [:span.descriptor label]]
    [box :size "6" :child
      [input-text :model value
                  :class "editor"
                  :on-change #(set-space-value! key %)]]]])

;; *********************** REPLACING ************************/
(defn member-sanitize [string]
  "Splits members string on newlines, commas, and spaces
   removing empty entries"
   (s/split string #"[\n,]+"))

(defn to-line [in-crowd index member]
  (let [id (:id member)
        selected (some #{id} in-crowd)]
    [:div {:class (if (nil? selected) "selected" "")
           ;; Hack to unset border-top for stylistic purposes
           :style (if (= index 0) #js {:border-top-style "none"})}
          (:name (get-member id))]))

;; *******^^^^^^********** REPLACING ***********^^^^^^*******/
(defn member-selector [label]
  [h-box :children [
    [box :justify :end :size "1" :child [:span.descriptor label]]
    [box :size "6" :class "member-selector"  :child
      [selection-list :model current-members
                      :choices (filter #(not= 0 (:id %)) (:members @app-state))
                      :label-fn #(:name %)
                      :width "600px"
                      :hide-border? true
                      :on-change #(reset! current-members %)]]]])

(defn edit-box
  [label value key rows & [sanitizer]]
    (h-box :children [
      [box :justify :end :size "1" :child [:span.descriptor label]]
      [box :size "6" :child
        [input-textarea :model value :rows (if rows rows 3) :class "editor"
          :on-change #(set-space-value! key ((or sanitizer identity) %))]]]))

(defn space-type [value t]
  "Radio button wrapper that sets space type when changed"
  (radio-button :model t :label (s/capitalize t) :value value :on-change #(set-space-value! :type t)))

(defn space-type-selector []
  (let [value (:type @current)]
    [h-box :children
     [[box   :size "1" :child [:div]]
     [v-box :size "6" :children
        (interpose [gap :size "15px"]
          (map (partial space-type value) ["welcome" "featured" "standard" "private"]))]]]))

(defn edit-page []
  ;; TODO (Nicholas): Clean-up this let block
  (let [index (session/get :current-index)
        space (if (= index -1) {:id (inc (apply max (map :id (:spaces @app-state))))
                                :title ""
                                :creator (session/get :current-user)
                                :text ""
                                :members []
                                :type "standard"}
                               (session/get :current-space))]
    (reset! current space)
    (reset! current-members (set (:members space)))
    (let [{:keys [id title creator text members type]} space]
      [container "edit-page"
       (interpose [gap :size "20px"] [
        [:h1 (str "Altspace Spaces Admin - " (if-not (= title "") title "New Space"))]
        [edit-line "Title" title :title]
        [edit-box "Description" text :text]
        [space-type-selector type]
        [member-selector]
        [h-box :class "edit-delete-cancel-save" :justify :between :children [
          [:div.delete
            (if (> index -1)
              [md-circle-icon-button :md-icon-name "md-delete" :size :larger :on-click #(delete id) :class "delete"])]
          [:div.aligner
            [:div.cancel-save
              [md-circle-icon-button :md-icon-name "md-cancel" :size :larger :on-click cancel       :class "cancel"]
              [md-circle-icon-button :md-icon-name "md-save"   :size :larger :on-click #(save id)   :class "save"]]]]]])])))
