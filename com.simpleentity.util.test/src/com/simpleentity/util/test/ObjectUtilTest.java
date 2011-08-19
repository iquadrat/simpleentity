/*
 * Created on Oct 6, 2007
 *
 */
package com.simpleentity.util.test;

import com.simpleentity.util.ObjectUtil;
import com.simpleentity.util.collection.CollectionUtil;

import junit.framework.TestCase;

public class ObjectUtilTest extends TestCase {
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void test_obj_equals() {
    String a = "x";
    String b = "y";
    String c = "x";
    
    assertTrue(ObjectUtil.equals(null, null));
    assertFalse(ObjectUtil.equals(null, a));
    assertFalse(ObjectUtil.equals(b, null));
    assertTrue(ObjectUtil.equals(a, a));
    assertFalse(ObjectUtil.equals(a, b));
    assertTrue(ObjectUtil.equals(a, c));
    assertFalse(ObjectUtil.equals(b, a));
    assertTrue(ObjectUtil.equals(b, b));
    assertFalse(ObjectUtil.equals(b, c));
    assertTrue(ObjectUtil.equals(c, a));
    assertFalse(ObjectUtil.equals(c, b));
    assertTrue(ObjectUtil.equals(c, c));
  }
  
  public void test_supertypes_of_object() {
    assertEquals(CollectionUtil.<Class<?>>setOf(Object.class), ObjectUtil.getAllSuperTypes(Object.class));
  }
  
  public void test_extended_class() {
    class CA {}
    class CB extends CA {}
    class CC extends CB {}
    
    assertEquals(CollectionUtil.<Class<?>>setOf(CA.class, Object.class), ObjectUtil.getAllSuperTypes(CA.class));    
    assertEquals(CollectionUtil.<Class<?>>setOf(CB.class, CA.class, Object.class), ObjectUtil.getAllSuperTypes(CB.class));    
    assertEquals(CollectionUtil.<Class<?>>setOf(CC.class, CB.class, CA.class, Object.class), ObjectUtil.getAllSuperTypes(CC.class));    
  }
  
  interface A {}
  interface B extends A {}
  interface C extends A {}
  interface D extends C,B {}
  
  public void test_interfaces() {
    assertEquals(CollectionUtil.<Class<?>>setOf(A.class), ObjectUtil.getAllSuperTypes(A.class));    
    assertEquals(CollectionUtil.<Class<?>>setOf(B.class, A.class), ObjectUtil.getAllSuperTypes(B.class));    
    assertEquals(CollectionUtil.<Class<?>>setOf(C.class, A.class), ObjectUtil.getAllSuperTypes(C.class));    
    assertEquals(CollectionUtil.<Class<?>>setOf(D.class, C.class, B.class, A.class), ObjectUtil.getAllSuperTypes(D.class));    
  }
  
}
