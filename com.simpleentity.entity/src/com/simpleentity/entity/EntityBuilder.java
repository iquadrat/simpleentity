package com.simpleentity.entity;

import javax.annotation.Nullable;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.id.EntityIdFactory;

public abstract class EntityBuilder<T extends Entity<T>> {

	@Nullable private final T entity;

	protected EntityBuilder(@CheckForNull T entity) {
		this.entity = entity;
	}

	public final T build(EntityIdFactory idFactory) {
		EntityId id = (entity == null) ? idFactory.newEntityId() : entity.getEntityId();
		return build(id);
	}

	protected abstract T build(EntityId id);

}
