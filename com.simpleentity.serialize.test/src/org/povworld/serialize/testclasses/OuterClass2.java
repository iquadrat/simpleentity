/*
 * Created on Jul 2, 2008
 */
package org.povworld.serialize.testclasses;

public class OuterClass2 {
  
  private class Inner {
  }
  
  public Object createInner() {
    return new Inner();
  }

}
