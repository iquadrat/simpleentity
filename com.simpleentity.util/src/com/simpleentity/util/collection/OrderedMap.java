package com.simpleentity.util.collection;


import java.util.*;

import com.simpleentity.util.Assert;

/**
 * Map that keeps insertion order of values.
 */
public class OrderedMap<K, V> {

  private final Map<K, V> fMap       = new HashMap<K, V>();
  private final List<V>   fValueList = new ArrayList<V>();
  private final List<K>   fKeyList   = new ArrayList<K>();

  public void put(K key, V value) {
    Assert.isFalse(fMap.containsKey(key), "key inserted twice");
    fMap.put(key, value);
    fKeyList.add(key);
    fValueList.add(value);
  }

  public void put(int index, K key, V value) {
    if (fMap.containsKey(key)) {
      Assert.fail("key inserted twice");
    }
    fMap.put(key, value);
    fKeyList.add(index, key);
    fValueList.add(index, value);
  }

  /**
   * @return the value to which this map maps the specified key, or
   *         <tt>null</tt> if the map contains no mapping for this key.
   */
  public V get(K key) {
    return fMap.get(key);
  }

  /**
   * Returns the value at the specified position in this ordered map.
   *
   * @param index index in the insertion order of the value to return
   * @return the value at the specified position
   * @throws IndexOutOfBoundsException if the index is out of range
   *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
   */
  public V getValue(int index) {
    return fValueList.get(index);
  }

  /**
   * Returns the key at the specified position in this ordered map.
   *
   * @param index index in the insertion order of the key to return
   * @return the key at the specified position
   * @throws IndexOutOfBoundsException if the index is out of range
   *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
   */
  public K getKey(int index) {
    return fKeyList.get(index);
  }

  /**
   * @return The number of elements in this map.
   */
  public int size() {
    return fValueList.size();
  }

  /**
   * For test-purposes only.
   */
  public void testInvariants() {
    Assert.isTrue(fValueList.size() == fMap.size(), "Map inconsistent: ", fValueList.size(), "!=",fMap.size());
    Assert.isTrue(fKeyList.size() == fMap.size(), "Map inconsistent: ",fKeyList.size() , "!=" , fMap.size() );
  }

  /**
   * @return iterator over the values in insertion order
   */
  public Iterator<V> valueIterator() {
    return fValueList.iterator();
  }

  /**
   * @return iterator over the keys in insertion order
   */
  public Iterator<K> keyIterator() {
    return fKeyList.iterator();
  }

  /**
   * @return a copy of the keys in the map
   */
  public List<K> keys() {
    return new ArrayList<K>(fKeyList);
  }

  public List<V> values() {
    return new ArrayList<V>(fValueList);
  }

  public boolean containsKey(K key) {
    return fMap.containsKey(key);
  }

  public boolean isEmpty() {
    return fMap.isEmpty();
  }

  @Override
  public String toString() {
    return "keys: " + fKeyList + " values: " + fValueList;
  }

  public void remove(K key) {
    Assert.isTrue(fMap.containsKey(key), "removed key not in map");
    V value = fMap.remove(key);
    fKeyList.remove(key);
    fValueList.remove(value);
  }

  public void clear() {
    fMap.clear();
    fKeyList.clear();
    fValueList.clear();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !obj.getClass().equals(getClass())) {
      return false;
    }
    OrderedMap<?, ?> otherMap = (OrderedMap<?, ?>)obj;
    return fKeyList.equals(otherMap.fKeyList) && fValueList.equals(otherMap.fValueList);
  }

  @Override
  public int hashCode() {
    return fKeyList.hashCode() + fValueList.hashCode();
  }

}
