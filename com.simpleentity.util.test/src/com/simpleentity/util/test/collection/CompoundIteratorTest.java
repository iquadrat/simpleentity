/*
 * Created on Oct 7, 2007
 *
 */
package com.simpleentity.util.test.collection;

import java.util.*;

import com.simpleentity.util.collection.CollectionUtil;
import com.simpleentity.util.collection.CompoundIterator;

import junit.framework.TestCase;

public class CompoundIteratorTest extends TestCase {

  @SuppressWarnings("unchecked")
  public void test_iterate_single_empty_iter() {
    Iterator<String> iter = new ArrayList<String>().iterator();
    
    CompoundIterator<String, Iterator<String>> cIter = new CompoundIterator<String, Iterator<String>>(CollectionUtil.listOf(iter));
    
    assertFalse(cIter.hasNext());
    assertFalse(cIter.hasNext());
    assertFalse(cIter.hasNext());
    assertFalse(cIter.hasNext());
  }
  
  @SuppressWarnings("unchecked")
  public void test_iterate_single_element_iter() {
    Iterator<String> iter = Arrays.asList("s1").iterator();
    
    List<Iterator<String>> iterList = CollectionUtil.listOf(iter);
    CompoundIterator<String, Iterator<String>> cIter = new CompoundIterator<String, Iterator<String>>(iterList);
    
    assertTrue(cIter.hasNext());
    assertEquals("s1", cIter.next());
    assertFalse(cIter.hasNext()); 
  }
  
  @SuppressWarnings("unchecked")
  public void test_iterate_multiple_empty_iters() {
    Iterator<String> iter1 = new ArrayList<String>().iterator();
    Iterator<String> iter2 = new ArrayList<String>().iterator();
    Iterator<String> iter3 = Arrays.asList("hello").iterator();
    Iterator<String> iter4 = new ArrayList<String>().iterator();
    
    List<Iterator<String>> iterList = Arrays.asList(iter1,iter2,iter3,iter4);
    CompoundIterator<String, Iterator<String>> cIter = new CompoundIterator<String, Iterator<String>>(iterList);
    
    assertTrue(cIter.hasNext());
    assertEquals("hello", cIter.next());
    assertFalse(cIter.hasNext());
  }
  
  @SuppressWarnings("unchecked")
  public void test_iterate_multiple_iters() {
    Iterator<String> iter1 = Arrays.asList("hello", "this").iterator();
    Iterator<String> iter2 = Arrays.asList("is").iterator();
    Iterator<String> iter3 = Arrays.asList("an", "extremely", "compound").iterator();
    Iterator<String> iter4 = Arrays.asList("iterator","sequence").iterator();
    
    List<Iterator<String>> iterList = Arrays.asList(iter1,iter2,iter3,iter4);
    CompoundIterator<String, Iterator<String>> cIter = new CompoundIterator<String, Iterator<String>>(iterList);
    
    assertTrue(cIter.hasNext());
    assertEquals("hello", cIter.next());
    assertTrue(cIter.hasNext());
    assertEquals("this", cIter.next());
    assertTrue(cIter.hasNext());
    assertEquals("is", cIter.next());
    assertTrue(cIter.hasNext());
    assertEquals("an", cIter.next());
    assertTrue(cIter.hasNext());
    assertEquals("extremely", cIter.next());
    assertTrue(cIter.hasNext());
    assertEquals("compound", cIter.next());
    assertTrue(cIter.hasNext());
    assertEquals("iterator", cIter.next());
    assertTrue(cIter.hasNext());
    assertEquals("sequence", cIter.next());
    assertFalse(cIter.hasNext());
  }
  @SuppressWarnings("unchecked")
  public void test_remove() {
    
    List<String> strings1 = new ArrayList<String>(Arrays.asList("hello", "this"));
    List<String> strings2 = new ArrayList<String>(Arrays.asList("is"));
    
    Iterator<String> iter1 = strings1.iterator();
    Iterator<String> iter2 = strings2.iterator();
    
    List<Iterator<String>> iterList = Arrays.asList(iter1,iter2);
    CompoundIterator<String, Iterator<String>> cIter = new CompoundIterator<String, Iterator<String>>(iterList);
    
    assertTrue(cIter.hasNext());
    assertEquals("hello", cIter.next());
    assertTrue(cIter.hasNext());
    assertEquals("this", cIter.next());
    
    cIter.remove();
    assertEquals(Arrays.asList("hello"), strings1);
    assertEquals(Arrays.asList("is"), strings2);
    
    assertTrue(cIter.hasNext());
    assertEquals("is", cIter.next());
    assertFalse(cIter.hasNext());
    
    cIter.remove();
    assertEquals(Arrays.asList("hello"), strings1);
    assertEquals(Arrays.asList(), strings2);
  }
  
}
