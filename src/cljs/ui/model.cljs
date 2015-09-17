(ns ui.model
  (:require [reagent.core :refer [atom]]
            [reagent.session :as session]))

(defonce app-state (atom
 {:spaces [{:title   "Buy cat food"
            :id      0
            :creator 0
            :type    "public"
            :text    "Abbers really needs some new food"}
           {:title   "Distributed Algorithms in TLA (Abstract)"
            :id      1
            :creator 1
            :type    "private"
            :text    "TLA (the temporal logic of actions) is a simple logic for describing and reasoning about concurrent systems. It provides a uniform way of specifying algorithms and their correctness properties, as well as rules for proving that one specification satisfies another. TLA+ is a formal specification language based on TLA, and TLC is a model checker for TLA+ specifications. TLA+ and TLC have been used to specify and check high-level descriptions of real, complex systems. Because TLA+ provides the full power of ordinary mathematics, it permits simple, straightforward specifications of the kinds of algorithms presented at PODC. This tutorial will try to convince you to describe your algorithms in TLA+. You will then be able to check them with TLC and use TLA to prove their correctness as formally or informally as you want. (However, TLA proofs do have one disadvantage that is mentioned below.) The tutorial will describe TLA+ through examples and demonstrate how to use TLC. No knowledge of TLA is assumed. TLA does have the following disadvantages: It can describe only a real algorithm, not a vague, incomplete sketch of an algorithm. You can specify an algorithm's correctness condition in TLA only if you understand what the algorithm is supposed to do. TLA makes it harder to cover gaps in a proof with handwaving. Some researchers may find these drawbacks insurmountable."}
           {:title   "Specifying and Verifying Fault-Tolerant Systems"
            :id      2
            :creator 7
            :type    "public"
            :text    "Assertional verification of concurrent systems began almost twenty years ago with the work of Ashcroft [4]. By the early 1980’s, the basic principles of formal specification and verification of concurrent systems were known [10, 12, 19]. More precisely, we had learned how to specify and verify those aspects of a system that can be expressed as the correctness of an individual execution. Faulttolerant systems are just one class of concurrent systems; they require no special techniques. The most important problems that remain are in the realm of engineering, not science. Scientific ideas must be translated into engineering practice. We describe here what we believe to be a suitable framework for an engineering discipline of formal specification and verification."}
           {:title   "The Slow Winter"
            :id      3
            :creator 9
            :type    "starred"
            :text    "According to my dad, flying in airplanes used to be fun. You could smoke on the plane, and smoking was actually good for you. Everybody was attractive, and there were no fees for anything, and there was so much legroom that you could orient your body parts in arbitrary and profane directions without bothering anyone, and you could eat caviar and manatee steak as you were showered with piles of money that were personally distributed by JFK and The Beach Boys. Times were good, assuming that you were a white man in the advertising business, WHICH MY FATHER WAS NOT SO PERHAPS I SHOULD ASK HIM SOME FOLLOW-UP QUESTIONS BUT I DIGRESS. The point is that flying in airplanes used to be fun, but now it resembles a dystopian bin-packing problem in which humans, carry-on luggage, and five dollar peanut bags compete for real estate while crying children materialize from the ether and make obscure demands in unintelligible, Wookie-like languages while you fantasize about who you won’t be helping when the oxygen masks descend."}]
  :members [{:id 0 :name "Admin Istrator"        :admin true  :gender "none"}
            {:id 1 :name "Nicholas van de Walle" :admin true  :gender "male"}
            {:id 9 :name "James Mickens"         :admin false  :gender "male"}]}))

;; Queries
(defn get-member [id]
  (first (filter #(= id (:id %)) (:members @app-state))))

;; Helpers
(defn positions
  "Finds the current indexes of items in a collection which
   pass a predicate. Used to locate spaces for updating"
  [pred coll]
  (keep-indexed (fn [idx x]
                  (when (pred x)
                    idx))
                coll))

;; TODO (Nicholas): cache this value, have it only be computed once
(defn gen-space-id [spaces]
  "Give us the next available space ID"
  (inc (apply max (map :id spaces))))

(defn auth [id]
  "Is this member an administrator?"
  (:admin (get-member id)))

;; Constructors
(defn make-space []
 {:id (inc (apply max (map :id (:spaces @app-state))))
  :text ""
  :title ""
  :members []
  :type "standard"
  :creator (session/get :current-user)})

;; Mutations
(defn add-space! [space]
  "Add an ID 1 higher than the current max space id,
   and put it at the end of :spaces"
  (let [to-save (assoc space :id (gen-space-id (:spaces @app-state)))]
    (swap! app-state assoc-in [:spaces] (conj (:spaces @app-state) to-save))))

(defn update-space! [space]
  "Saves the current temp space into the DB atom
   Beware of this tight coupling, uses current index
   as well as the ID. Consider turning :spaces into a
   map instead of a vector"
  (let [index (first (positions #(= (:id space) (:id %)) (:spaces @app-state)))]
    (swap! app-state update-in [:spaces index] #(merge % space))))

(defn remove-space! [id]
  "filter the space out by id"
  (swap! app-state assoc-in [:spaces] (filterv #(not= id (:id %)) (:spaces @app-state))))
