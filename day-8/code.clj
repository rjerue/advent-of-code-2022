#!/usr/bin/env bb
(require '[clojure.string :as str])

(def args
  (if (seq *command-line-args*)
    (first *command-line-args*)
    (str (.getParent (io/file *file*)) "/ex.txt")))

(def base
  (->> args
       (slurp)
       (str/split-lines)
       (mapv (fn [n] (mapv #(Character/digit % 10) n)))))


(def col-count (count base))

(defn vertical [r c fnc]
  (rest
   (loop [r* r
          list []]
     (if (or (= r* -1)
             (= r* col-count))
       list
       (recur (fnc r*)
              (into list [(-> base (nth r*) (nth c))]))))))

(defn can-see? [col n]
  (or
   (empty? col)
   (and (nil? (some (set #{n}) col))
        (= n (apply max (conj col n))))))

(defn part-1 [num left right up down]
  (when (some true? (map #(can-see? % num) [left right up down]))
    num))

(defn trees-vis [col n]
  (loop [index 0]
    (if-let [tree (nth col index nil)]
      (if (>= tree n)
        (inc index)
        (recur (inc index)))
      index)))

(defn part-2 [num left right up down]
  (->> [left right up down]
       (map #(trees-vis % num))
       (apply *)))

(defn solution [fnc]
  (map-indexed
   (fn [r, row]
     (map-indexed
      (fn [c, num]
        (let [left (rseq (subvec row 0 c))
              right (subvec row (inc c) (count row))
              up (vertical r c inc)
              down (vertical r c dec)]
          (fnc num left right up down)))
      row)) base))

(println "part 1" (count (filter some? (flatten (solution part-1)))))
(println "part 2" (apply max (flatten (solution part-2))))
