/*
 * Created on Dec 8, 2007
 *
 */
package org.povworld.serialize.name;

import junit.framework.*;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for org.povworld.serialize.name");
    //$JUnit-BEGIN$
    suite.addTestSuite(StringPoolTest.class);
    suite.addTestSuite(ClassNameRepositoryTest.class);
    //$JUnit-END$
    return suite;
  }

}
