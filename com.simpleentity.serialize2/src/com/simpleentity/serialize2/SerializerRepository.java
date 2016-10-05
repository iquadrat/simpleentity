package com.simpleentity.serialize2;

import java.lang.reflect.Field;

import net.jcip.annotations.ThreadSafe;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.collection.CollectionSerializer;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.Type;

@ThreadSafe
public interface SerializerRepository {

	public Type getDeclaredType(Field field);

	public EntityId getMetaDataId(Class<?> class_);

	public MetaData getMetaData(Class<?> class_);

	public MetaData getMetaData(EntityId metaDataId);

	public Serializer<?> getSerializer(EntityId metaDataId);

	public CollectionSerializer<?> getCollectionSerializer(EntityId metaDataId);

}
