/*
 * Created on Dec 8, 2007
 *
 */
package org.povworld.serialize;

import junit.framework.*;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for org.povworld.serialize");
    //$JUnit-BEGIN$
    suite.addTestSuite(JavaClassesSerializableTest.class);
    suite.addTestSuite(AWTClassesSerializableTest.class);
    suite.addTestSuite(ObjectGraphSerializerTest.class);
    //$JUnit-END$
    suite.addTest(org.povworld.serialize.name.AllTests.suite());
    return suite;
  }

}
