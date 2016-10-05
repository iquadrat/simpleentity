package com.simpleentity.serialize2;

import net.jcip.annotations.ThreadSafe;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.collection.CollectionSerializer;

@ThreadSafe
public interface SerializerRepository {

	public Serializer<?> getSerializer(EntityId metaDataId);

	public CollectionSerializer<?> getCollectionSerializer(EntityId metaDataId);
}
