package com.simpleentity.util.collection;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.ThreadSafe;

import com.simpleentity.util.Assert;

/**
 * A list of listeners. Helper for the implementation of the observer/observable pattern.
 * <p>
 * NOTE: Add and remove of listeners is O(number of listeners).
 *
 * @param <T> Listener type.
 */
@ThreadSafe
public class ListenerList<T> implements Iterable<T> {

  private static class Handle<T> implements IListenerHandle {

    private final List<T> fListeners;

    private final T       fListener;

    public Handle(T listener, List<T> listeners) {
      fListener = listener;
      fListeners = listeners;
    }

    @Override
    public void remove() {
      boolean succ = fListeners.remove(fListener);
      Assert.isTrue(succ, "Double dispose of listener detected!");
    }

  }

  /**
   * The list of registered listeners.
   */
  private final List<T> fListeners = new CopyOnWriteArrayList<T>();

  /**
   * Registers the specified listener.
   *
   * @return a handle which allows to unregister the given listener
   */
  public IListenerHandle add(T listener) {
    fListeners.add(listener);
    return new Handle<T>(listener, fListeners);
  }

  /**
   * @return iterator over a snapshot of the list
   */
  @Override
  public Iterator<T> iterator() {
    return fListeners.iterator();
  }

  /**
   * @return <code>true</code> iff there are any listeners registered
   */
  public boolean isEmpty() {
    return fListeners.isEmpty();
  }

  /**
   * @return the number of registered listeners
   */
  public int size() {
    return fListeners.size();
  }

  @Override
  public String toString() {
    return fListeners.toString();
  }

}
