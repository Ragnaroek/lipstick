(ns lipstick.ast.visitor.visitors)

(gen-class
  :extends org.eclipse.jdt.core.dom.ASTVisitor
  :name TypeDeclVisitor
  :prefix "-typedecl-"
  :state typeList
  :init init
  :methods [[visit [Object] boolean]
            [getTypes [] java.util.List]])

(defn- -typedecl-init ([] [[] (atom [])]))

(defn- add-type [this decl]
   (swap! (.typeList this) conj decl))

(defn- -typedecl-visit-TypeDeclaration [this decl]
  (add-type this decl)
  true)

(defn- -typedecl-visit-AnonymousClassDeclaration [this decl]
  (add-type this decl)
  true)

(defn- -typedecl-getTypes [this]
  @(.typeList this))


(gen-class
  :extends org.eclipse.jdt.core.dom.ASTVisitor
  :name ConstructorVisitor
  :prefix "-constructor-"
  :state constList
  :init init
  :methods [[visit [Object] boolean]
            [getConstructors [] java.util.List]])

(defn- -constructor-init ([] [[] (atom [])]))

(defn- add-constructor [this method]
  (swap! (.constList this) conj method))

(defn- -constructor-visit-MethodDeclaration [this method]
  (when (.isConstructor method)
    (add-constructor this method))
  true)

(defn- -constructor-getConstructors [this]
  @(.constList this))
