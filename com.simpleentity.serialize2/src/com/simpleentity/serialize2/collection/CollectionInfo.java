package com.simpleentity.serialize2.collection;

import org.povworld.collection.Collection;
import org.povworld.collection.CollectionUtil;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.value.ValueObject;
import com.simpleentity.serialize2.generic.ObjectInfo;

public class CollectionInfo extends ValueObject {
	private final ObjectInfo collectionInfo;
	private final EntityId elementMetaDataId;
	private final int elementCount;
	private final Iterable<?> elements;

	public CollectionInfo(ObjectInfo collectionInfo, EntityId elementMetaDataId, Collection<?> elements) {
		this.collectionInfo = collectionInfo;
		this.elementMetaDataId = elementMetaDataId;
		this.elementCount = elements.size();
		this.elements = elements;
	}

	public CollectionInfo(ObjectInfo collectionInfo, EntityId elementMetaDataId, int elementCount, Iterable<?> elements) {
		this.collectionInfo = collectionInfo;
		this.elementMetaDataId = elementMetaDataId;
		this.elementCount = elementCount;
		this.elements = elements;
	}

	public ObjectInfo getCollectionInfo() {
		return collectionInfo;
	}

	public EntityId getElementMetaDataId() {
		return elementMetaDataId;
	}

	public int getElementCount() {
		return elementCount;
	}

	public Iterable<?> getElements() {
		return elements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((collectionInfo == null) ? 0 : collectionInfo.hashCode());
		result = prime * result + elementCount;
		result = prime * result + ((elementMetaDataId == null) ? 0 : elementMetaDataId.hashCode());
		// TODO add elements hashcode
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO simplify
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		CollectionInfo other = (CollectionInfo) obj;
		if (collectionInfo == null) {
			if (other.collectionInfo != null)
				return false;
		} else if (!collectionInfo.equals(other.collectionInfo))
			return false;
		if (elementCount != other.elementCount)
			return false;
		if (elementMetaDataId == null) {
			if (other.elementMetaDataId != null)
				return false;
		} else if (!elementMetaDataId.equals(other.elementMetaDataId))
			return false;
		if (!CollectionUtil.iteratesEqualSequence(elements, other.elements)) {
			return false;
		}
		return true;
	}



}