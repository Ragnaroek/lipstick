(ns lipstick.ast.query
  (:use lipstick.ast.visitor.visitors))

(defn- run-visitor [visitor node]
  (. node accept visitor)
  visitor)

(defn compilation-unit [file-name]
  (. (new de.defmacro.ast.JavaParser) parseCompilationUnit
   (new java.io.File file-name)))

(defn compilation-unit-from-string [str]
  (. (new de.defmacro.ast.JavaParser) parseCompilationUnit str))

(defn type-declarations [cu]
  (. (run-visitor (new TypeDeclVisitor) cu) getTypes))

(defn constructors [types]
  (let [visitor (new ConstructorVisitor)]
    (doall (map (partial run-visitor visitor) types))
    (. visitor getConstructors)))

(defn javadoc [nodes]
  (let [visitor (new JavaDocVisitor)]
    (doall (map (partial run-visitor visitor) nodes))
    (. visitor get)))
