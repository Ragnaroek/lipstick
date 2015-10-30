(ns lipstick.core
  (:use lipstick.ast.query))

(defn foo []
  (println (type-declarations (. (new de.defmacro.ast.JavaParser) parseCompilationUnit (new java.io.File "/Users/mb/SmsAdvertisingElementServiceImpl.java")))))

(defn -main [& args]
   (foo))
