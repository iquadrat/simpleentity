package com.simpleentity.entity.id;

import net.jcip.annotations.Immutable;

import org.povworld.collection.common.MathUtil;
import org.povworld.collection.common.ObjectUtil;

import com.simpleentity.annotation.ValueObject;

@Immutable
@ValueObject
public class EntityId {

	private static final int HASH_CONSTANT = 0xa9ff883e;

	private final long id;

	public EntityId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return HASH_CONSTANT + MathUtil.hashLong(id);
	}

	@Override
	public boolean equals(Object object) {
		EntityId other = ObjectUtil.castOrNull(object, EntityId.class);
		if (other == null) {
			return false;
		}
		return this.id == other.id;
	}

	@Override
	public String toString() {
		return "EntityId[" + id + "]";
	}
}
