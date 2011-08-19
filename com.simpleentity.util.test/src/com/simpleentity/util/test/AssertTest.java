/*
 * Created on Oct 7, 2007
 *
 */
package com.simpleentity.util.test;

import com.simpleentity.util.Assert;
import com.simpleentity.util.AssertionFailedError;

import junit.framework.TestCase;

public class AssertTest extends TestCase {

  public void test_fail_throws() {
    try {
      Assert.fail("message");
      fail("exception expected");
    } catch(AssertionFailedError e) {
      // pass
      assertEquals(e.getMessage(), "message");
      assertNull(e.getCause());
    }
  }
  
  public void test_message_is_composed() {
    try {
      Assert.fail("some thing id=",42," went terribly wrong");
      fail("exception expected");
    } catch(AssertionFailedError e) {
      // pass
      assertEquals(e.getMessage(), "some thing id=42 went terribly wrong");
      assertNull(e.getCause());
    }
  }
  
  public void test_fail_with_throwable() {
    Throwable t = new Throwable();
    try {
      Assert.fail(t);
      fail("exception expected");
    } catch(AssertionFailedError e) {
      // pass
      assertEquals(e.getCause(),t);
    }
  }
  
  public void test_assertNotNull() {
    String s = "hello";
    Assert.isNotNull(s);
    Assert.isNotNull(s, "message");
    
    s = null;
    try{
      Assert.isNotNull(s);
      fail("exception expected");
    } catch (AssertionFailedError e) {
      // pass
    }
    
    try{
      Assert.isNotNull(s, "this is ","the cause");
      fail("exception expected");
    } catch (AssertionFailedError e) {
      // pass
      assertEquals("this is the cause", e.getMessage());
    }
    
  }
  
  public void test_assertNull() {
    String s = null;
    Assert.isNull(s);
    Assert.isNull(s, "message");
    
    s = "world";
    try{
      Assert.isNull(s);
      fail("exception expected");
    } catch (AssertionFailedError e) {
      // pass
    }
    
    try{
      Assert.isNull(s, "this is ","the cause");
      fail("exception expected");
    } catch (AssertionFailedError e) {
      // pass
      assertEquals("this is the cause", e.getMessage());
    }
    
  }
  
  public void test_assert_equals() {
    String a1 = "foo";
    String a2 = "foo";
    String b = "bar";
    Assert.equals(a1,a1);
    Assert.equals(a1,a2);
    Assert.equals(a2,a1);
    
    try { 
      Assert.equals(a1, b);
      fail("exception expected");
    } catch (AssertionFailedError e) {
      // pass
    }
  }
  
  public void test_assert_true() {
    Assert.isTrue(true);
    try { 
      Assert.isTrue(false);
      fail("exception expected");
    } catch (AssertionFailedError e) {
      // pass
    }
    
    Assert.isTrue(true, "cause");
    try { 
      Assert.isTrue(false, "cause");
      fail("exception expected");
    } catch (AssertionFailedError e) {
      // pass
      assertEquals("cause", e.getMessage());
    }
  }
  
  public void test_assert_false() {
    Assert.isFalse(false);
    try { 
      Assert.isFalse(true);
      fail("exception expected");
    } catch (AssertionFailedError e) {
      // pass
    }
    
    Assert.isFalse(false, "cause");
    try { 
      Assert.isFalse(true, "cause");
      fail("exception expected");
    } catch (AssertionFailedError e) {
      // pass
      assertEquals("cause", e.getMessage());
    }
  }
  
}
