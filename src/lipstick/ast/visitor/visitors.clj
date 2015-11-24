(ns lipstick.ast.visitor.visitors)

(defn- prefix-name [prefix]
   (str "-" prefix "-"))

(defmacro gen-collector [name prefix type]
  `(do
    (gen-class
      :extends org.eclipse.jdt.core.dom.ASTVisitor
      :name ~name
      :prefix ~(symbol (prefix-name prefix))
      :state ~'state
      :init ~'init
      :methods [[~'visit [~'Object] ~'boolean]
                [~'get [] java.util.List]])

    (defn- ~(symbol (str (prefix-name prefix) "init")) ([] [[] (atom [])]))

    (defn- ~(symbol (str (prefix-name prefix) "visit-" type)) [~'this ~'o]
      (swap! (.state ~'this) conj ~'o)
      true)

    (defn- ~(symbol (str (prefix-name prefix) "get")) [~'this]
      @(.state ~'this))))

(gen-collector JavaDocVisitor "javadoc" Javadoc)

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
