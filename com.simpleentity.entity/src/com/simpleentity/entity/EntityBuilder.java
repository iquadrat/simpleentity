package com.simpleentity.entity;

import javax.annotation.Nullable;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.id.IdFactory;

public abstract class EntityBuilder<T extends Entity<T>> {
	
	@Nullable private final T entity;

	protected EntityBuilder() {
		this.entity = null;
	}
	
	protected EntityBuilder(T entity) {
		this.entity = entity;
	}
	
	public final T build(IdFactory idFactory) {
		EntityId id = (entity == null) ? idFactory.newEntityId() : entity.getEntityId();
		return build(id);
	}
	
	protected abstract T build(EntityId id);

}
