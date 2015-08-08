(ns ui.model
  (:require [reagent.core :refer [atom]]))

;; TODO (Nicholas) update members to use member IDs instead
;; Store member data in its own model atom
(defonce app-state (atom
  {:spaces [{:name    "Welcome Space"
             :creator "Admin Istrator"
             :members ["Nicholas" "Dana" "Bruce" "Ari"]
             :text    "Welcome to Altspace! Use this space to get acquainted with the interface"}
            {:name    "Welcome Space"
             :creator "Admin Istrator"
             :members ["Nicholas" "Dana" "Cymatic Bruce" "Ari"]
             :text    "Welcome to Altspace! Usasdfkhaksjdhfgkjashgdfjkhasdgfe this space to get acquainted with the interface"}
            {:name    "Welcome Space"
             :creator "Admin Istrator"
             :members ["Dana" "Ari"]
             :text    "Welcome to Altspace! Use this space to get acquainted with the interface"}]}))
