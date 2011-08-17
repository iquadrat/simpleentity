/*
 * Created on 07.12.2006
 */
package com.simpleentity.util.collection;

import java.util.*;

/**
 * Implements a hash map that maps keys of type <tt>K</tt> to a set of type <tt>V</tt>.
 * A key cannot map to an empty set, that is, when the last element of a list is removed,
 * the corresponding key is removed as well. The set of values for a key is unordered.
 * 
 * @author micha
 */
public class MultiMapUnOrdered<K, V> extends AbstractMultiMap<K, V> {
  
  private static final int     DEFAULT_SET_SIZE = 4;
  
  private final Map<K, Set<V>> fMap             = new HashMap<K, Set<V>>();
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    MultiMapUnOrdered<?, ?> other = (MultiMapUnOrdered<?, ?>)obj;
    if (!fMap.equals(other.fMap)) return false;
    return true;
  }
  
  /**
   * @return the unmodifiable collection of values associated with the given key.
   *         If the key is not in the map, an empty collection is returned.
   */
  @Override
  public Set<V> get(K key) {
    if (!contains(key)) return Collections.emptySet();
    return Collections.unmodifiableSet(internalGet(key));
  }

  @Override
  public int hashCode() {
    return fMap.hashCode() + 4710013;
  }
  
  @Override
  public String toString() {
    return fMap.toString();
  }
  
  protected Set<V> createSet() {
    return new HashSet<V>(DEFAULT_SET_SIZE);
  }
  
  @Override
  protected Map<K, ? extends Set<V>> getMap() {
    return fMap;
  }
  
  @Override
  protected Set<V> getOrCreate(K key) {
    Set<V> result = fMap.get(key);
    if (result == null) {
      result = createSet();
      fMap.put(key, result);
    }
    return result;
  }
  
  /**
   * Returns the list of values associated with key.
   * @pre the map contains the key
   */
  @Override
  protected Set<V> internalGet(K key) {
    Set<V> result = getMap().get(key);
    if (result == null) throw new IllegalStateException("key "+key+" not found");
    return result;
  }
  
}
