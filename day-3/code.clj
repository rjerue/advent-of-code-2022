#!/usr/bin/env bb
(require '[clojure.string :as str]
         '[clojure.set :as set])
(import (java.util.regex Pattern))

(def args
  (if (seq *command-line-args*)
    (first *command-line-args*)
    (str (.getParent (io/file *file*)) "/ex.txt")))

(def base
  (->> args
       (slurp)
       (str/split-lines)))

(def az->v
  (map-indexed
   (fn [index item]
     [(-> item char) (inc index)]) (range 97 123)))

(def AZ->v
  (map-indexed
   (fn [index item]
     [(-> item char) (+ 27 index)]) (range 65 91)))

(def letter->v (into {} (into AZ->v az->v)))

(defn prioritize [transformer]
  (->>
   base
   (transformer)
   (map #(map set %))
   (map #(apply set/intersection %))
   (reduce into [])
   (map #(get letter->v %))
   (apply +)))

(defn divideInto2 [compartment]
  (re-seq (Pattern/compile (str ".{1," (/ (count compartment) 2) "}")) compartment))

(println "Part 1: " (prioritize #(map divideInto2 %)))
(println "Part 2: " (prioritize #(partition 3 %)))
