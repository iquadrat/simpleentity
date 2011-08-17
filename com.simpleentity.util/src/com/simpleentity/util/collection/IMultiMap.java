package com.simpleentity.util.collection;

import java.util.*;


public interface IMultiMap<K,V> {
  
  /**
   * @return true if the map contains given key
   */
  public boolean contains(K key);
  
  /**
   * @return true if the list associated with given key contains given value
   */
  public boolean contains(K key, V value);
  
  /**
   * @return the unmodifiable collection of values associated with the given key.
   *         If the key is not in the map, an empty collection is returned.
   */
  public Collection<V> get(K key);
  
  /**
   * @return true if the map contains no keys
   */
  public boolean isEmpty();
  
  /**
   * @return an iterable over the keys
   */
  public Iterable<K> keyIterable();
  
  /**
   * @return a duplication of the current key set
   */
  public Set<K> keySet();
  
  /**
   * @return the number of keys in the map
   */
  public int numberOfKeys();
 
  /**
   * @return number of values associated with given key. Returns 0 if the key is not in the map
   */
  public int numberOfValues(K key);
  
}
