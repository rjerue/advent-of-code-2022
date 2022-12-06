#!/usr/bin/env bb
(require '[clojure.string :as str])

(def args
  (if (seq *command-line-args*)
    (first *command-line-args*)
    (str (.getParent (io/file *file*)) "/ex.txt")))

(def base
  (->> args
       (slurp)
       (str/split-lines)))

(defn find-unique [input window-size]
  (loop [index 0]
    (let [range (+ index window-size)
          window (subs input index range)]
      (if (= window-size (count (set window)))
        range
        (recur (inc index))))))

(println "Part 1" (map #(find-unique % 4) base))
(println "Part 2" (map #(find-unique % 14) base))
