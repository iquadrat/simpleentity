package com.simpleentity.db;

import javax.annotation.Nullable;

import com.simpleentity.db.transaction.ReadWriteSession;

public abstract class EntityBuilder<T extends Entity<T>> {
	
	@Nullable private final T entity;

	protected EntityBuilder() {
		this.entity = null;
	}
	
	protected EntityBuilder(T entity) {
		this.entity = entity;
	}
	
	public final T build(ReadWriteSession session) {
		return (entity == null) ? createEntity(session) : modifyEntity(session);
	}
	
	private T createEntity(ReadWriteSession session) {
		T result = createInstance(session.newEntityId());
		session.create(result);
		return result;
	}
	
	private T modifyEntity(ReadWriteSession session) {
		T result = createInstance(entity.getEntityId());
		session.modify(result);
		return result;
	}
	
	protected abstract T createInstance(EntityId id);

}
