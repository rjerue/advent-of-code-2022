#!/usr/bin/env bb
(require '[clojure.string :as str]
         '[clojure.set :as set])

(def args
  (if (seq *command-line-args*)
    (first *command-line-args*)
    (str (.getParent (io/file *file*)) "/ex.txt")))

(defn split-up [input]
  (->> (str/split input #",")
       (map #(str/split % #"-"))))

(defn tuple->rangeset [[left right]]
  (set (range (Integer/parseInt left) (inc (Integer/parseInt right)))))

(defn row->rangeset-row [row]
  (->> row
       (map tuple->rangeset)
       (sort-by count)))

(defn row-subset [row]
  (loop [head (first row)
         tail (rest row)]
    (cond
      (empty? tail) row
      (set/subset? head (first tail)) (recur (first tail) (rest tail)))))

(defn intersections [row]
  (->> row (apply set/intersection) (not-empty)))

(def base
  (->> args
       (slurp)
       (str/split-lines)
       (map split-up)
       (map row->rangeset-row)))

(defn puzzle [keep-fn]
  (->> base (keep keep-fn) (count)))

(println "part 1" (puzzle row-subset))
(println "part 2" (puzzle intersections))
