package com.simpleentity.entity;

import net.jcip.annotations.Immutable;

import org.povworld.collection.common.ObjectUtil;

import com.simpleentity.entity.id.EntityId;

// TODO remove T
@Immutable
public abstract class Entity<T extends Entity<T>> {

	private static final int HASH_CONSTANT = 0xdc2e681f;

	public static String ID_FIELD_NAME = "entityId";

	private final EntityId entityId;

	protected Entity(EntityId id) {
		this.entityId = id;
	}

	public EntityId getEntityId() {
		return entityId;
	}

	public abstract EntityBuilder<T> toBuilder();

	@Override
	public final int hashCode() {
		return HASH_CONSTANT + entityId.hashCode();
	}

	@Override
	public final boolean equals(Object object) {
		Entity<?> other = ObjectUtil.castOrNull(object, Entity.class);
		if (other == null) {
			return false;
		}
		return this.entityId.equals(other.entityId);
	}
}
