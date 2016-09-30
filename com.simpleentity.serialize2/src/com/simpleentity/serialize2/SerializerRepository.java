package com.simpleentity.serialize2;

import java.lang.reflect.Field;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.collection.CollectionSerializer;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.Type;

public interface SerializerRepository {

	public Type getDeclaredType(Field field);

	public EntityId getMetaDataId(Class<?> class_);

	public MetaData getMetaData(Class<?> class_);

	public MetaData getMetaData(EntityId metaDataId);

	// Binary serializers:
	public BinarySerializer<Object> getPrimitiveSerializer(EntityId metaDataId);

	public BinarySerializer<EntityId> getEntityIdSerializer();

	public BinarySerializer<ObjectInfo> getBinarySerializer(EntityId metaDataId);

	// Java serializers:
	public Serializer<?> getSerializer(EntityId metaDataId);

	public CollectionSerializer<?> getCollectionSerializer(EntityId metaDataId);

}
