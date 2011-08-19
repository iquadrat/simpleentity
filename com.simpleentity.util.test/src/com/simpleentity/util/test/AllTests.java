/*
 * Created on 01.08.2007
 *
 */
package com.simpleentity.util.test;

import junit.framework.*;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for org.povworld.util");
    //$JUnit-BEGIN$
    suite.addTestSuite(IOUtilTest.class);
    suite.addTestSuite(AbstractDisposableTest.class);
    suite.addTestSuite(AssertTest.class);
    suite.addTestSuite(ObjectUtilTest.class);
    //$JUnit-END$
    suite.addTest(com.simpleentity.util.test.collection.AllTests.suite());
    return suite;
  }

}
