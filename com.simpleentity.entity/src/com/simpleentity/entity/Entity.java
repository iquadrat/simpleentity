package com.simpleentity.entity;

import net.jcip.annotations.Immutable;

import org.povworld.collection.common.ObjectUtil;

import com.simpleentity.entity.id.EntityId;

@Immutable
public abstract class Entity<T extends Entity<T>> {
	
	private static final int HASH_CONSTANT = 0xdc2e681f;
	
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
	
	public abstract EntityBuilder<T> toBuilder();

	@Override
	public final int hashCode() {
		return HASH_CONSTANT + id.hashCode();
	}

	@Override
	public final boolean equals(Object object) {
		Entity<?> other = ObjectUtil.castOrNull(object, Entity.class);
		if (other == null) {
			return false;
		}
		return this.id.equals(other.id) && this.type.equals(other.type);
	}
}
