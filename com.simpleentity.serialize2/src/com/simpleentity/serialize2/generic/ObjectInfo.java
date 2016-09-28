package com.simpleentity.serialize2.generic;

import org.povworld.collection.Map;
import org.povworld.collection.common.ObjectUtil;
import org.povworld.collection.mutable.HashMap;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.generic.GenericValue.CollectionValue;
import com.simpleentity.serialize2.generic.GenericValue.EntityIdValue;
import com.simpleentity.serialize2.generic.GenericValue.PrimitiveValue;
import com.simpleentity.serialize2.generic.GenericValue.ValueObjectValue;
import com.simpleentity.util.BuilderUtil;

public class ObjectInfo {
	// TODO add full MetaData instead just its EntityId?
	private final EntityId metaDataId;

	// TODO replace with an Immutable equivalent
	private final Map<String, GenericValue> entries;

	private ObjectInfo(Builder builder) {
		this.metaDataId = BuilderUtil.requiredBuilderField("metaTypeId", builder.metaDataId);
		this.entries = builder.entries;
	}

	public EntityId getMetaTypeId() {
		return metaDataId;
	}

	@CheckForNull
	public GenericValue getValue(String entryId) {
		return entries.get(entryId);
	}

	@CheckForNull
	public CollectionValue getCollectionValue(String entryId) {
		return getTypedFieldValue(entryId, CollectionValue.class);
	}

	@CheckForNull
	public PrimitiveValue getPrimiviteValue(String entryId) {
		return getTypedFieldValue(entryId, PrimitiveValue.class);
	}

	@CheckForNull
	public EntityIdValue getEntityIdValue(String entryId) {
		return getTypedFieldValue(entryId, EntityIdValue.class);
	}

	@CheckForNull
	public ValueObjectValue getValueObjectValue(String entryId) {
		return getTypedFieldValue(entryId, ValueObjectValue.class);
	}

	@CheckForNull
	private <T extends GenericValue> T getTypedFieldValue(String fieldId, Class<T> type) {
		GenericValue fieldValue = getValue(fieldId);
		if (fieldValue == null) {
			return null;
		}
		T collectionValue = ObjectUtil.castOrNull(fieldValue, type);
		if (collectionValue == null) {
			throw new SerializerException("Expected " + type.getSimpleName() + " but got " + fieldValue);
		}
		return collectionValue;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		@CheckForNull
		private EntityId metaDataId = null;
		private final HashMap<String, GenericValue> entries = new HashMap<>();

		public Builder setMetaDataId(EntityId metaDataId) {
			this.metaDataId = metaDataId;
			return this;
		}

		public Builder setEntryValue(String entryId, GenericValue value) {
			this.entries.put(entryId, value);
			return this;
		}

		public ObjectInfo build() {
			return new ObjectInfo(this);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		result = prime * result + ((metaDataId == null) ? 0 : metaDataId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectInfo other = (ObjectInfo) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		if (metaDataId == null) {
			if (other.metaDataId != null)
				return false;
		} else if (!metaDataId.equals(other.metaDataId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ObjectInfo [metaDataId=" + metaDataId + ", entries=" + entries + "]";
	}

}
