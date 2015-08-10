(ns ui.edit
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [clojure.string :as s]
            [re-com.core :refer [h-box v-box box gap]]
            [re-com.buttons :refer [button]]
            [re-com.misc :refer [input-text input-textarea radio-button]]
            [ui.model :refer [app-state]]
            [ui.common :refer [container]])
  (:import goog.History))

(defonce current (atom {:title "" :creator "" :text "" :members [] :type "standard"}))
;; Action Helpers
;; TODO (Nicholas): Remove duplication of actions between edit and core
(defn add-space! [space]
  (swap! app-state assoc-in [:spaces] (conj (:spaces @app-state) space)))

(defn remove-space! [index state]
  (swap! state assoc-in [:spaces]
    (let [spaces (:spaces @state)]
      (cond
        (= 1 (count spaces)) []
        (= (dec (count spaces)) index) (subvec spaces 0 index)
        (= index 0) (subvec spaces 1)
        :else (vec (concat (subvec spaces 0 index)
                     (subvec spaces (inc index))))))))

(defn update-space! [index]
  "Saves the current temp space into the DB atom
   Beware of this tight coupling"
  (js/console.log (str "updating # " index))
  (js/console.log (pr-str (:members @current)))
  (swap! app-state update-in [:spaces index] #(merge % @current))
  (js/console.log (pr-str (:members (nth (:spaces @app-state) index)))))

(defn set-space-value! [key value]
  "Sets a key on the current space, used by the on-change events"
  (swap! current assoc-in [key] value))

;; Actions
(defn save [index]
  "Also known as update or add"
  (js/console.log index)
  (if (> index -1)
    (update-space! index)
    (add-space! @current))
  (js/window.location.assign "#/"))

(defn cancel [index]
  (let [url (str "#/")]
    (js/window.location.assign url)))

(defn delete [index]
  (remove-space! index app-state)
  (js/window.location.assign "#/"))

;; EDIT PAGE
;; NOTE: Keep 600px in alignment with cancel-save
(defn edit-line [title value key]
  "An input-text with a label"
  [h-box :children [
    [box :justify :end :size "1" :child [:span.descriptor title]]
    [box :size "6" :child
      [input-text :model value
                  :width "600px"
                  :on-change #(set-space-value! key %)]]]])

(defn member-sanitize [string]
  "Splits members string on newlines, commas, and spaces
   removing empty entries"
   (js/console.log (pr-str (s/split string #"[\n\s,]+")))
   (s/split string #"[\n\s,]+"))

(defn edit-box
  [title value key rows & [sanitizer]]
    (h-box :children [
      [box :justify :end :size "1" :child [:span.descriptor title]]
      [box :size "6" :child
        [input-textarea :model value :rows (if rows rows 3) :width "600px"
          :on-change #(set-space-value! key ((or sanitizer identity) %))]]]))

(defn space-type [value t]
  (radio-button :model t :label (s/capitalize t) :value value :on-change #(set-space-value! :type t)))

(defn space-type-selector []
  (let [value (:type @current)]
    [h-box :children
     [[box   :size "1" :child [:div]]
     [v-box :size "6" :children
        (interpose [gap :size "15px"]
          (map (partial space-type value) ["standard" "private" "featured" "welcome"]))]]]))

(defn edit-page []
  ;; TODO (Nicholas): Clean-up this let block
  (let [index (int (session/get :current-space))
        space (if (= index -1) {:title "" :creator "" :text "" :members [] :type "private"}
                               (nth (:spaces @app-state) index))]
    (reset! current space)
    (let [{:keys [title creator text members type]} space]
      (js/console.log type)
      [container "edit-page"
       (interpose [gap :size "20px"] [
        [:h1 (str "Altspace Spaces Admin - " (if title title "Unnamed"))]
        [edit-line "Title" title :title]
        [edit-box "Description" text :text]
        [space-type-selector type]
        [edit-box "Members" (apply str (interpose "\n" members)) :members (count members) member-sanitize]
        [h-box :class "edit-delete-cancel-save" :justify :between :children [
          [:div.delete (if (> index -1) [button :label "Delete" :on-click #(delete index) :class "btn-danger"])]
          [:div.aligner
            [:div.cancel-save
              [button :label "Cancel" :on-click #(cancel index) :class "btn-default cancel"]
              [button :label "Save"   :on-click #(save index)   :class "btn-primary"]]]]]])])))
