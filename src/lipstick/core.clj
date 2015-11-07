(ns lipstick.core
  (:use lipstick.ast.query))

(defn foo []
  (println "docs" (map (fn [n] (. n toString)) (javadoc (constructors (type-declarations
    (compilation-unit "/Users/mb/SmsAdvertisingElementServiceImpl.java")))))))

(defn -main [& args]
   (foo))
