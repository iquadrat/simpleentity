/*
 * Created on 07.12.2006
 */
package com.simpleentity.util.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract implementation of a map that can associate multiple values with a single key.
 * 
 * @author micha
 *
 * @param <K> Type of the keys.
 * @param <V> Type of the values.
 */
public abstract class AbstractMultiMap<K, V> implements IMultiMap<K, V> {
  
  /**
   * Removes all entries of the map.
   * @post isEmpty()==true
   */
  public void clear() {
    getMap().clear();
  }
  
  @Override
  public boolean contains(K key) {
    return getMap().containsKey(key);
  }
  
  @Override
  public boolean contains(K key, V value) {
    return getMap().containsKey(key) && internalGet(key).contains(value);
  }
  
  @Override
  public boolean isEmpty() {
    return getMap().isEmpty();
  }
  
  @Override
  public Iterable<K> keyIterable() {
    return getMap().keySet();
  }
  
  @Override
  public Set<K> keySet() {
    return new HashSet<K>(getMap().keySet());
  }
  
  @Override
  public int numberOfKeys() {
    return getMap().size();
  }
  
  @Override
  public int numberOfValues(K key) {
    Collection<V> values = getMap().get(key);
    return (values == null) ? 0 : values.size();
  }
  
  /**
   * Adds the given value to the collection associated with the given key.
   */
  public boolean put(K key, V value) {
    if (value == null) throw new IllegalArgumentException();
    return getOrCreate(key).add(value);
  }
  
  /**
   * Appends values to the end of the list associated with the given key.
   */
  public void putAll(K key, Iterable<V> values) {
    if (!values.iterator().hasNext()) return;
    Collection<V> coll = getOrCreate(key);
    for(V value: values) {
      coll.add(value);
    }
  }
  
  /**
   * Removes all values associated with the given key.
   * @return <tt>true</tt> iff the collection has changed
   */
  public boolean remove(K key) {
    Collection<V> list = getMap().remove(key);
    return list != null;
  }
  
  /**
   * Removes the first occurrence of given value from the collection associated with given key.
   * 
   * @return <tt>true</tt> iff the collection has changed.
   */
  public boolean remove(K key, V value) {
    if (!contains(key)) return false;
    Collection<V> list = internalGet(key);
    if (!list.remove(value)) return false;
    if (list.isEmpty()) getMap().remove(key);
    return true;
  }
  
  /**
   * Removes all values from the list associated with the given key.
   * 
   * TODO test
   * 
   * @param key the key whose values are removed
   * @param values the values to remove
   * @return <code>true</code> if values have been removed
   */
  public boolean removeAll(K key, Iterable<V> values) {
    if (!contains(key)) return false;
    Collection<V> list = internalGet(key);
    int size = list.size();
    
    for(V value: values) {
      list.remove(value);
    }
    if (list.isEmpty()) getMap().remove(key);
    
    return size != list.size();
  }
  
  /**
   * @return the map that is used to store the entries
   */
  protected abstract Map<K, ? extends Collection<V>> getMap();
  
  protected abstract Collection<V> getOrCreate(K key);
  
  /**
   * Returns the list of values associated with key.
   * @pre the map contains the key
   */
  protected abstract Collection<V> internalGet(K key);
  
}
