/*
 * Created on Oct 6, 2007
 *
 */
package com.simpleentity.util.test.collection;

import junit.framework.*;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for org.povworld.util.collection");
    //$JUnit-BEGIN$
    suite.addTestSuite(CompoundIteratorTest.class);
    //$JUnit-END$
    return suite;
  }

}
