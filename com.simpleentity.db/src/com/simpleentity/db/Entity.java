package com.simpleentity.db;

import com.simpleentity.db.session.ReadWriteSession;

public abstract class Entity<T extends Entity<T>> {
	
	private final EntityId id;
	private final Class<T> type;
	
	protected Entity(Class<T> type, EntityId id) {
		this.type = type;
		this.id = id;
	}
	
	public Class<T> getType() {
		return type;
	}

	public EntityId getEntityId() {
		return id;
	}
	
	public abstract EntityBuilder<T> modify();
	
	public void delete(ReadWriteSession session) {
		session.delete(this);
	}
	
	// TODO implement equals and hashcode
	
}
