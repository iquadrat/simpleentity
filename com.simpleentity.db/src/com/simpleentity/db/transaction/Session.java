package com.simpleentity.db.transaction;

import java.io.Closeable;

import com.simpleentity.db.Entity;
import com.simpleentity.db.EntityId;

public interface Session extends Closeable {

	public <T extends Entity<T>> T getEntity(Class<T> type, EntityId id);

	public boolean hasChanges();

	public boolean isHead();

	/**
	 * @throws IllegalStateException
	 *             if it is a head session
	 * @see #isHead()
	 */
	public Session getBase() throws IllegalStateException;
	
	public void close();

}
