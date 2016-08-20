package com.simpleentity.db.session;

import java.io.Closeable;

import com.simpleentity.db.Entity;
import com.simpleentity.db.EntityId;
import com.simpleentity.util.collection.ListenerHandle;

public interface Session extends Closeable {

	public <T extends Entity<T>> T getEntity(Class<T> type, EntityId id);

	public boolean hasChanges();

	public boolean isHead();
	
	public void sync();
	
	public ReadWriteSession fork();

	/**
	 * @throws IllegalStateException
	 *             if it is a head session
	 * @see #isHead()
	 */
	public Session getBase() throws IllegalStateException;
	
	public ListenerHandle addChangeListener(ChangeListener changeListener);
	
	public void close();

}
