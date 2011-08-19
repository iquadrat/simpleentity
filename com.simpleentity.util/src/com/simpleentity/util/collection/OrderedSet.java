package com.simpleentity.util.collection;


import java.util.*;

import com.simpleentity.util.Assert;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * A generic ordered set of items.
 *
 * @author micha
 *
 * @param <T> Type of elements.
 */
public final class OrderedSet<T> implements Iterable<T> {

  private static final int USE_SET_MIN_SIZE = 14;

  private final List<T> fList;

  @CheckForNull
  private Set<T>  fSet = null;

  public OrderedSet() {
    fList = new ArrayList<T>();
  }

  public OrderedSet(int size) {
    fList = new ArrayList<T>(size);
    if (size >= USE_SET_MIN_SIZE) {
      fSet = new HashSet<T>(size);
    }
  }

  public OrderedSet(T... elements) {
    List<T> elementArray = Arrays.asList(elements);
    fList = new ArrayList<T>(elementArray);
    if (elements.length >= USE_SET_MIN_SIZE) {
      fSet = new HashSet<T>(elementArray);
    }
  }

  /**
   * Adds all elements in the iteration.
   */
  public void add(Iterable<T> elements) {
    for(T element: elements) {
      add(element);
    }
  }

  /**
   * Adds all elements in the iteration.
   */
  public void add(Iterator<T> elements) {
    while (elements.hasNext()) {
      add(elements.next());
    }
  }

  public void add(T element) {
    if (contains(element)) {
      return;
    }
    fList.add(element);
    addElementToSet(element);
  }

  private void addElementToSet(T element) {
    if (fSet == null && fList.size() >= USE_SET_MIN_SIZE) {
      fSet = new HashSet<T>(USE_SET_MIN_SIZE, 0.9f);
      fSet.addAll(fList);
    }
    if (fSet != null) {
      fSet.add(element);
    }
  }

  /**
   * @param index location, where <tt>element</tt> is added. Starts at 0.
   */
  public void add(int index, T element) {
    Assert.isFalse(contains(element), "element inserted twice");
    fList.add(index, element);
    addElementToSet(element);
  }

  public int size() {
    return fList.size();
  }

  public boolean isEmpty() {
    return fList.isEmpty();
  }

  public boolean contains(T element) {
    return (fSet != null) ? fSet.contains(element) : fList.contains(element);
  }

  /**
   * For test-purposes only.
   */
  public void testInvariants() {
    if (fSet == null) {
      return;
    }
    Assert.isTrue(fList.size() == fSet.size(), "Map inconsistent: ",fList.size(),"!=",fSet.size());
  }

  @Override
  public Iterator<T> iterator() {
    return asUnmodifiableList().iterator();
  }

  /**
   * @pre size() > 0
   * @return first element in the list.
   */
  public T getFirstElement() {
    return fList.get(0);
  }

  @Override
  public String toString() {
    return fList.toString();
  }

  public T get(int index) {
    return fList.get(index);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (!o.getClass().equals(getClass())) {
      return false;
    }
    OrderedSet<?> otherSet = (OrderedSet<?>)o;
    return fList.equals(otherSet.fList);
  }

  @Override
  public int hashCode() {
    return fList.hashCode();
  }

  /**
   * Removes all elements in this set.
   */
  public void clear() {
    fList.clear();
    if (fSet != null) {
      fSet.clear();
    }
  }

  public Set<T> asUnmodifiableSet() {
    if (fSet == null) {
      return new HashSet<T>(fList);
    }
    return Collections.unmodifiableSet(fSet);
  }

  public List<T> asUnmodifiableList() {
    return Collections.unmodifiableList(fList);
  }

  public boolean remove(T element) {
    if (!contains(element)) {
      return false;
    }
    fList.remove(element);
    if (fSet != null) {
      fSet.remove(element);
    }
    return true;
  }

}
