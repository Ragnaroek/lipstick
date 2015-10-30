(ns lipstick.ast.query
  (:use lipstick.ast.visitor.visitors))

(defn run-visitor [visitor node]
  (. node accept visitor)
  visitor)

(defn type-declarations [cu]
  (. (run-visitor (new TypeDeclVisitor) cu) getTypes))

(defn constructors [types]
  (let [visitor (new ConstructorVisitor)]
    (doall (map (partial run-visitor visitor) types))
    (. visitor getConstructors)))

; TODO JavaDoc aus Knoten rausziehen
