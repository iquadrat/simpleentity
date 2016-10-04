package com.simpleentity.serialize2.collection;

import org.povworld.collection.Collection;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.value.ValueObject;
import com.simpleentity.serialize2.generic.ObjectInfo;

public class CollectionInfo<C> extends ValueObject {
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
}