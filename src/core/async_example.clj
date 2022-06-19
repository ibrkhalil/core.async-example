(ns core.async-example
  (:require [clojure.core.async :refer [chan thread close! put! go <! >! sliding-buffer dropping-buffer take! <!! >!!]]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

;; Channels

(comment
  (let [c (chan)]
    (future (>!! c 42))
    (future (println (<!! c)))))

  (let [c (chan)]
    (future (dotimes [x 10]
              (>!! c x)))
    (future (dotimes [x 10]
              (>!! c x)))
    (future (dotimes [x 20]
              (println (<!! c)))))

(comment
  (let [c (chan)]
    (put! c 42 (fn [v] (println "Sent: " v)))
    (take! c (fn [v] (println "Got: " v)))))

(comment
  (let [c (chan (dropping-buffer 1))]
    @(future
       (dotimes [x 3]
         (>!! c x)
         (println "SENT " x))
       (println "DONE"))
    (future
      (dotimes [x 3]
        (println "GOT " x))
      (println "DONE"))))

(comment
  (let [c (chan (sliding-buffer 1))]
    @(future
       (dotimes [x 3]
         (>!! c x)
         (println "SENT " x))
       (println "DONE"))
    (future
      (dotimes [x 3]
        (println "GOT " x))
      (println "DONE"))))

(comment 
  (let [c (chan 2)]
    (future 
      (dotimes [x 2]
        (>!! c x))
      (close! c)
      (println "CLOSED"))
    (future 
      (loop []
        (when-some [v (<!! c)]
          (println "GOT: " v)
          (recur)))
      (println "EXITING"))))


; Thread
(<!! (thread 42
     (let [t1 (thread "Thread 1")
           t2 (thread "Thread 2")]
          [(<!! t1)
           (<!! t2)])))

(let [c (chan)]
     (thread
       (dotimes [x 3]
                (>!! c x)
                (println "Put: " x)))
     (thread
       (dotimes [x 3]
                (println "Got: " (<!! c)))))

(<!! (go 42))

(let [c (chan)]
     (go (dotimes [x 3]
                  (>! c x)
                  (println "Put: " x)))
     (go (dotimes [x 3]
                  (println "Got: " (<! c)))))

(let [c (chan)]
     (go (doseq [x (range 3)]
                (>! c x)))
     (go (dotimes [x 3]
                  (println "Got: " (<! c)))))