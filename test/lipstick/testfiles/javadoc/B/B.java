
/**B**/
public class B {
  /**Konstruktor**/
  public B() {}

  public static class BInner {
     /**Inner Konstruktor**/
     public BInner() {
     }
  }

  /**BInner2**/
  public static class BInner2 {
     /**Konstruktor   **/ /* <- the trailing whitespace makes this a different contructor*/
     public BInner2() {}
  }
}

/**B2**/
class B2 {
   /**B2 Konstruktor**/
   B2() {
   }
}
