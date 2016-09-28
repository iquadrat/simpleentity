package com.simpleentity.serialize2.meta;

import org.povworld.collection.common.PreConditions;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.value.ValueObject;

@ValueObject
public class Entry {

	private final String fieldId;
	private final EntityId cardinality;
	private final EntityId typeMetaDataId;

	public Entry(String id,  EntityId cardinality, EntityId typeMetaData) {
		this.fieldId = PreConditions.paramNotNull(id);
		this.cardinality = cardinality;
		this.typeMetaDataId = PreConditions.paramNotNull(typeMetaData);
	}

	public String getId() {
		return fieldId;
	}

	public EntityId getCardinality() {
		return cardinality;
	}

	public EntityId getDeclaredTypeId() {
		return typeMetaDataId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardinality == null) ? 0 : cardinality.hashCode());
		result = prime * result + ((fieldId == null) ? 0 : fieldId.hashCode());
		result = prime * result + ((typeMetaDataId == null) ? 0 : typeMetaDataId.hashCode());
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
		Entry other = (Entry) obj;
		if (cardinality != other.cardinality)
			return false;
		if (fieldId == null) {
			if (other.fieldId != null)
				return false;
		} else if (!fieldId.equals(other.fieldId))
			return false;
		if (typeMetaDataId == null) {
			if (other.typeMetaDataId != null)
				return false;
		} else if (!typeMetaDataId.equals(other.typeMetaDataId))
			return false;
		return true;
	}

}
