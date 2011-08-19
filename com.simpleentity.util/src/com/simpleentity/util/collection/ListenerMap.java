package com.simpleentity.util.collection;

import java.util.ArrayList;
import java.util.List;

import com.simpleentity.util.Assert;
import com.simpleentity.util.disposable.IDisposable;

/**
 * A map of listeners. Facilitates the implementation of the observer/observable pattern.
 * <p>
 * NOTE: This class is thread-safe.
 *
 * @param <K> key type.
 * @param <T> Listener type.
 */
public class ListenerMap<K, T> {

  /**
   * A list holding the registered listeners.
   */
  private final MultiMapUnOrdered<K, T> fListeners = new MultiMapUnOrdered<K, T>();

  /**
   * Adds the specified listener to the list of listeners.
   *
   * @return a handle which allows to remove the listener from the list
   */
  public synchronized IDisposable add(final K key, final T listener) {
    fListeners.put(key, listener);
    return new IDisposable() {
      @Override
      public synchronized void dispose() {
        boolean result = fListeners.remove(key, listener);
        Assert.isTrue(result, "double remove of handler");
      }
    };
  }

  /**
   * @return a duplicate of the list of listeners
   */
  public synchronized List<T> getListeners(K key) {
    return new ArrayList<T>(fListeners.get(key));
  }

  /**
   * @return <code>true</code> if there are listeners in the list
   */
  public synchronized boolean isEmpty() {
    return fListeners.isEmpty();
  }

}
