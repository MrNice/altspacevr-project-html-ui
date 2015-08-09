(ns ui.model
  (:require [reagent.core :refer [atom]]))

;; TODO (Nicholas) update members to use member IDs instead
;; Store member data in its own model atom
(defonce app-state (atom
  {:spaces [{:title   "Welcome Space"
             :creator "Admin Istrator"
             :type    "welcome"
             :members ["Nicholas" "Dana" "Bruce" "Ari"]
             :text    "Welcome to Altspace! Use this space to get acquainted with the interface"}
            {:title   "Nicholas's hovel"
             :creator "Nicholas"
             :type    "private"
             :members ["Nicholas" "Dana" "Cymatic Bruce" "Ari"]
             :text    "This is where clojurescript junkies tend to fester"}
            {:title   "Origin Featured Space"
             :creator "Kirk"
             :type    "featured"
             :members ["Nicholas" "Dana" "Cymatic Bruce" "Ari"]
             :text    "Welcome to Altspace! Usasdfkhaksjdhfgkjashgdfjkhasdgfe this space to get acquainted with the interface"}
            {:title   "USS Enterprise"
             :creator "Matt Jeffries"
             :type    "welcome"
             :members ["Spock" "Kirk" "Uhura" "Scottie"]
             :text    "To boldy go, where no man has gone before"}]}))