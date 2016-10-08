package com.simpleentity.serialize2.collection.common;

import java.lang.reflect.Field;

import org.povworld.collection.mutable.ArrayList;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.collection.CollectionInfo;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.MetaDataRepository;
import com.simpleentity.serialize2.meta.Type;

public class ArrayListSerializer extends AbstractCollectionSerializer<ArrayList<Object>> {

	private final ObjectInfo arrayListInfo;
	private final MetaDataRepository metaDataRepository;

	public ArrayListSerializer(EntityId metaDataId, MetaDataRepository metaDataRepository) {
		super(ArrayList.class,
				newMetaDataBuilder()
					.setClassName(ArrayList.class.getName())
					.build(metaDataId));
		this.metaDataRepository = metaDataRepository;
		arrayListInfo = ObjectInfo.newBuilder()
				.setMetaDataId(metaDataId)
				.build();
	}

	@Override
	protected ObjectInfo getObjectInfo(ArrayList<Object> collection) {
		return arrayListInfo;
	}

	@Override
	public ArrayList<Object> deserialize(CollectionInfo collectionInfo) {
		ArrayList<Object> result = new ArrayList<>(collectionInfo.getElementCount());
		for(Object element: collectionInfo.getElements()) {
			result.push(element);
		}
		return result;
	}

	@Override
	public Type getElementType(Field field) {
		Class<?> declaredElementType = getElementMetaDataId(field);
		return new Type(metaDataRepository.getMetaDataId(declaredElementType), false);
	}
}
