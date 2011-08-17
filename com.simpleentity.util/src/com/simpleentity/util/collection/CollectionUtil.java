/*
 * Created on 01.08.2007
 *
 */
package com.simpleentity.util.collection;

import java.util.*;

public class CollectionUtil {

  private CollectionUtil() {
  }

  public static <T> List<T> listOf(T... elements) {
    return Arrays.asList(elements);
  }

  public static <T> Set<T> setOf(T... elements) {
    HashSet<T> result = new HashSet<T>(elements.length);
    result.addAll(Arrays.asList(elements));
    return result;
  }

  public static boolean iteratesEqualSequence(Iterator<?> iter1, Iterator<?> iter2) {
    while(iter1.hasNext()) {
      
      if (!iter2.hasNext()) return false;
      Object o1 = iter1.next();
      Object o2 = iter2.next();
      if (!o1.equals(o2)) return false;
      
    }
    
    if (iter2.hasNext()) return false;
    
    return true;
  }

}
