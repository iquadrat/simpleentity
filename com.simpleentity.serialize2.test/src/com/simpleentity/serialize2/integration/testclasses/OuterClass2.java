package com.simpleentity.serialize2.integration.testclasses;

public class OuterClass2 {

  private class Inner {
  }

  public Object createInner() {
    return new Inner();
  }

}
