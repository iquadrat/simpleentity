package com.simpleentity.util.collection;

import net.jcip.annotations.GuardedBy;

import org.povworld.collection.persistent.PersistentMultiMapUnOrdered;
import org.povworld.collection.persistent.PersistentMultiMapUnOrderedImpl;
import org.povworld.collection.persistent.PersistentUnOrderedSet;

import com.simpleentity.util.Assert;
import com.simpleentity.util.disposable.IDisposable;

/**
 * A map of listeners. Facilitates the implementation of the observer/observable
 * pattern.
 * <p>
 * NOTE: This class is thread-safe.
 *
 * @param <K>
 *            key type.
 * @param <T>
 *            Listener type.
 */
public class ListenerMap<K, T> {
	/**
	 * A list holding the registered listeners.
	 */
	@GuardedBy("this")
	private PersistentMultiMapUnOrdered<K, T> listeners = new PersistentMultiMapUnOrderedImpl<>();

	/**
	 * Adds the specified listener to the list of listeners.
	 *
	 * @return a handle which allows to remove the listener from the list
	 */
	public synchronized IDisposable add(final K key, final T listener) {
		listeners = listeners.with(key, listener);
		return new IDisposable() {
			@Override
			public synchronized void dispose() {
				PersistentMultiMapUnOrdered<K, T> newListeners = listeners.without(key, listener);
				Assert.isTrue(listeners != newListeners, "Double remove of listener handler!");
				listeners = newListeners;
			}
		};
	}

	/**
	 * @return a duplicate of the listeners currently associated with
	 *         {@code key}
	 */
	public synchronized PersistentUnOrderedSet<T> getListeners(K key) {
		return listeners.get(key);
	}

	/**
	 * @return <code>true</code> if there are listeners in the map
	 */
	public synchronized boolean isEmpty() {
		return listeners.isEmpty();
	}
}
