(ns lipstick.core
  (:use lipstick.ast.query))

(defn foo []
  (println "constructors" (. (constructors (type-declarations
    (. (new de.defmacro.ast.JavaParser) parseCompilationUnit
    (new java.io.File "/Users/mb/SmsAdvertisingElementServiceImpl.java")))) size)))

(defn -main [& args]
   (foo))
