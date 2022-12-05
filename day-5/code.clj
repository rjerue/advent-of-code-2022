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
       (partition-by empty?)))

(def raw-stack (first base))

(defn parse-stack-count [input]
  (->> input (re-seq #"(\d+)") (last) (last) (Integer/parseInt)))

(defn to-letter-index [index]
  (inc (* 4 index)))

(def parsed-stacks
  (let [rows (reverse (butlast raw-stack))
        stack-count (parse-stack-count (last raw-stack))]
    (->>
     rows
     (reduce
      (fn [stacks row]
        (reduce
         (fn [stacks* index]
           (if-let [letter (some-> row
                                   (nth (to-letter-index index) nil)
                                   (str)
                                   (str/trim)
                                   (not-empty))]
             (update stacks* index #(conj % letter))
             stacks*))
         stacks
         (range 0 stack-count)))
      (mapv (fn [_] (vector)) (range 0 stack-count)))
     (map reverse)
     (mapv vec))))

(def instructions
  (->> (last base)
       (map (fn [match]
              (map
               #(Integer/parseInt %)
               (rest (re-matches #"move (\d+) from (\d+) to (\d+)" match)))))))

(defn do-stacks [reversed?]
  (reduce
   (fn [stacks [count start end]]
     (let [to-move (subvec (nth stacks (dec start) []) 0 count)
           to-move* (if reversed? (reverse to-move) to-move)]
       (-> stacks
           (update (dec start) #(vec (drop count %)))
           (update (dec end) #(into (vec to-move*) %)))))
   parsed-stacks
   instructions))

(println "Part 1 " (apply str (map first (do-stacks true))))
(println "Part 2" (apply str (map first (do-stacks false))))
