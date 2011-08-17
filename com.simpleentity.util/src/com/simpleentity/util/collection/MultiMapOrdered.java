/*
 * Created on 31.10.2005
 */
package com.simpleentity.util.collection;

import java.util.*;
import java.util.Map.Entry;

/**
 * Implements a hash map that maps keys of type <tt>K</tt> to a list of type <tt>V</tt>.
 * A key cannot map to an empty list, that is, when the last element of a list is removed,
 * the corresponding key is removed as well. The list of values for a key is stored in
 * insertion-order and may contain a value multiple times.
 * 
 * @author micha
 */
public class MultiMapOrdered<K, V> extends AbstractMultiMap<K, V> implements IMultiMapOrdered<K, V> {
  
  private static final int      DEFAULT_MAP_SIZE  = 16;
  private static final int      DEFAULT_LIST_SIZE = 2;
  
  private final Map<K, List<V>> fMap;
  
  public static <K, V> IMultiMapOrdered<K, V> emptyMap() {
    return new MultiMapOrdered<K, V>();
  }
  
  /**
   * Creates an empty ordered multi-map with default size.
   */
  public MultiMapOrdered() {
    this(DEFAULT_MAP_SIZE);
  }
  
  /**
   * Creates an empty ordered multi-map with the given size hint.
   * @param size the expected size of the map
   */
  public MultiMapOrdered(int size) {
    fMap = new LinkedHashMap<K, List<V>>(size, 0.9f);
  }
  
  /**
   * Creates a new ordered multi-map which gets initialized by a copy of
   * the given <code>base</code> map.
   */
  public MultiMapOrdered(MultiMapOrdered<K, V> base) {
    // make a deep copy
    this(base.fMap.size());
    for (Entry<K, List<V>> entry: base.fMap.entrySet()) {
      fMap.put(entry.getKey(), new ArrayList<V>(entry.getValue()));
    }
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    MultiMapOrdered<?, ?> other = (MultiMapOrdered<?, ?>)obj;
    if (!fMap.equals(other.fMap)) return false;
    return true;
  }
  
  @Override
  public List<V> get(K key) {
    if (!contains(key)) return Collections.emptyList();
    return Collections.unmodifiableList(internalGet(key));
  }
  
  @Override
  public int hashCode() {
    return fMap.hashCode() + 17581;
  }
  
  /**
   * Inserts value at given position into the list associated with given key.
   * The value is inserted after the (first occurrence of the) <tt>previous</tt> element.
   * 
   * @pre <tt>previous</tt> exists in the list
   */
  public void putAfter(K key, V value, V previous) {
    if (!contains(key, previous)) {
      throw new IllegalArgumentException("previous not found in list");
    }
    
    List<V> list = getOrCreate(key);
    int index = list.indexOf(previous);
    list.add(index + 1, value);
  }
  
  /**
   * Appends value to the beginning of the list associated with given key.
   */
  public void putAtBegin(K key, V value) {
    getOrCreate(key).add(0, value);
  }
  
  /**
   * Appends value to the end of the list associated with given key.
   */
  public void putAtEnd(K key, V value) {
    getOrCreate(key).add(value);
  }
  
  /**
   * Inserts value at given position into the list associated with given key.
   * The value is inserted  before the (first occurrence of the) <tt>next</tt> element.
   * 
   * @pre <tt>next</tt> exists in the list
   */
  public void putBefore(K key, V value, V next) {
    if (!contains(key, next)) {
      throw new IllegalArgumentException("next not found in list");
    }
    
    List<V> list = getOrCreate(key);
    int index = list.indexOf(next);
    list.add(index, value);
  }
  
  /**
   * Replaces in the list associated with <tt>key</tt> all occurrences
   * of <tt>oldValue</tt> (all elements <tt>e</tt> with <tt>e.equals(oldValue)</tt>)
   * with <tt>newValue</tt>.
   * 
   * @param key key that references the list
   * @param oldValue old value to replace
   * @param newValue new value to replace with
   * @return <tt>true</tt> if the map has changed, that is, at least one replacement has occurred
   */
  public boolean replaceAll(K key, V oldValue, V newValue) {
    if (!contains(key)) return false;
    List<V> list = fMap.get(key);
    
    boolean changed = false;
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).equals(oldValue)) {
        list.set(i, newValue);
        changed = true;
      }
    }
    return changed;
  }
  
  @Override
  public String toString() {
    return fMap.toString();
  }
  
  @Override
  protected Map<K, ? extends Collection<V>> getMap() {
    return fMap;
  }
  
  /**
   * Returns the list of values associated with key. If there is no list
   * for given key, a new, empty list is created and inserted into the map.
   */
  @Override
  protected List<V> getOrCreate(K key) {
    List<V> result = fMap.get(key);
    if (result == null) {
      result = new ArrayList<V>(DEFAULT_LIST_SIZE);
      fMap.put(key, result);
    }
    return result;
  }
  
  /**
   * Returns the list of values associated with key.
   * @pre the map contains the key
   */
  @Override
  protected List<V> internalGet(K key) {
    List<V> result = fMap.get(key);
    if (result == null) throw new IllegalStateException();
    return result;
  }
  
}
