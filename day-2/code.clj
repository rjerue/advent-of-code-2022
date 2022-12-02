#!/usr/bin/env bb
(require '[clojure.string :as str])

(defn index-of [coll e] (first (keep-indexed #(when (= e %2) %1) coll)))

(def args
  (if (seq *command-line-args*)
    (first *command-line-args*)
    (str (.getParent (io/file *file*)) "/ex.txt")))

(def base
  (->> args
       (slurp)
       (str/split-lines)
       (map #(str/split % #" "))))

(def play-score
  {"X" 1
   "Y" 2
   "Z" 3})

(def win-scores
  {"X" 0
   "Y" 3
   "Z" 6})

(def left->right
  {"A" "X"   ;rock
   "B" "Y"   ;paper
   "C" "Z"}) ;scissors

(defn outcome1 [[left right]]
  (cond
    (= (get left->right left) right) 3
    (case left
      "A" (= right "Y")
      "B" (= right "Z")
      "C" (= right "X")) 6
    :else 0))

(defn outcome2 [[left right]]
  (get play-score
       (get left->right
            (let [dominance ["A" "B" "C" "A" "B" "C"]]
              (case right
                "Y" left
                "X" (get dominance (+ 2 (index-of dominance left)))
                "Z" (get dominance (+ 1 (index-of dominance left))))))))

(defn play [mapping outcome]
  (reduce
   (fn [sum [left right]]
     (+ sum (get mapping right) (outcome [left right])))
   0
   base))

(println "Part 1" (play play-score outcome1))
(println "Part 2" (play win-scores outcome2))
