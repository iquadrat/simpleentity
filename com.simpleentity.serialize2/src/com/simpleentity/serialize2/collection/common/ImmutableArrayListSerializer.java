package com.simpleentity.serialize2.collection.common;

import java.lang.reflect.Field;

import org.povworld.collection.immutable.ImmutableArrayList;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.collection.CollectionInfo;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.MetaDataRepository;
import com.simpleentity.serialize2.meta.Type;

public class ImmutableArrayListSerializer extends AbstractCollectionSerializer<ImmutableArrayList<Object>> {

	private final ObjectInfo arrayListInfo;
	private final MetaDataRepository metaDataRepository;

	public ImmutableArrayListSerializer(EntityId metaDataId, MetaDataRepository metaDataRepository) {
		super(ImmutableArrayList.class,
				newMetaDataBuilder()
					.setClassName(ImmutableArrayList.class.getName())
					.build(metaDataId));
		this.metaDataRepository = metaDataRepository;
		arrayListInfo = ObjectInfo.newBuilder()
				.setMetaDataId(metaDataId)
				.build();
	}

	@Override
	protected ObjectInfo getObjectInfo(ImmutableArrayList<Object> collection) {
		return arrayListInfo;
	}

	@Override
	public ImmutableArrayList<Object> deserialize(CollectionInfo collectionInfo) {
		return ImmutableArrayList
				.newBuilder(collectionInfo.getElementCount())
				.addAll(collectionInfo.getElements())
				.build();
	}

	@Override
	public Type getElementType(Field field) {
		Class<?> declaredElementType = getElementMetaDataId(field);
		return new Type(metaDataRepository.getMetaDataId(declaredElementType), false);
	}
}
