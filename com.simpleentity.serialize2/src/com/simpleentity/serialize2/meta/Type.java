package com.simpleentity.serialize2.meta;

import javax.annotation.CheckForNull;

import org.povworld.collection.common.PreConditions;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.value.ValueObject;

public final class Type extends ValueObject {

	private final EntityId metaDataId;
	private final boolean optional;
	private final @CheckForNull Type elementType;

	public Type(EntityId metaDataId, boolean optional) {
		this(metaDataId, optional, null);
	}

	public Type(EntityId metaDataId, boolean optional, @CheckForNull Type elementType) {
		PreConditions.paramNotNull(metaDataId);
		this.metaDataId = metaDataId;
		this.optional = optional;
		this.elementType = elementType;
	}

	public boolean isOptional() {
		return optional;
	}

	public EntityId getMetaDataId() {
		return metaDataId;
	}

	public Type getElementType() {
		return elementType;
	}

	public boolean isCollectionType() {
		return elementType != null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append('[');
		if (optional) {
			sb.append("(optional)");
		}
		sb.append(metaDataId.getId());
		if (isCollectionType()) {
			sb.append('[');
			sb.append(getElementType());
			sb.append(']');
		}
		sb.append(']');
		return sb.toString();
	}

}
