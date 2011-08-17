package com.simpleentity.util.collection;

import java.util.List;

public interface IMultiMapOrdered<K, V> extends IMultiMap<K,V> {
  
  /**
   * @return the unmodifiable collection of values associated with the given key.
   *         If the key is not in the map, an empty collection is returned.
   */
  @Override
  public List<V> get(K key);
   
}