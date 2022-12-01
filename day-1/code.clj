#!/usr/bin/env bb
(require '[clojure.string :as str])

(defn add [nums]
  (->> nums
       (map #(Integer/parseInt %))
       (reduce + 0)))

(def args
  (if (seq *command-line-args*)
    (first *command-line-args*)
    (str (.getParent (io/file *file*)) "/ex.txt")))

(def base
  (->> args
       (slurp)
       (str/split-lines)
       (partition-by #(= "" %))
       (filter #(> (count (first %)) 0))
       (map add)
       (sort)))

(println "Part 1" (first (take-last 1 base)))
(println "Part 2" (reduce + 0 (take-last 3 base)))
