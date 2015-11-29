(ns lipstick.ast.helper)

(defn node-source [file-content node]
  "Extracts the original source code of 'node' in the original source file"
  (. file-content substring (. node getStartPosition) (+ (. node getStartPosition) (. node getLength))))
