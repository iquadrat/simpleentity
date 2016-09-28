package com.simpleentity.serialize2;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.collection.CollectionSerializer;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.Cardinality;
import com.simpleentity.serialize2.meta.MetaData;

public interface SerializerRepository {

	public EntityId getMetaDataId(Class<?> class_);

	public MetaData getMetaData(Class<?> class_);

	public MetaData getMetaData(EntityId metaDataId);

	public Cardinality getCardinality(EntityId cardinality);

	// Binary serializers:
	public BinarySerializer<?> getPrimitiveSerializer(EntityId metaDataId);

	public BinarySerializer<EntityId> getEntityIdSerializer();

	public BinarySerializer<ObjectInfo> getBinarySerializer(EntityId metaDataId);

	// Generic serializers:
	public Serializer<?> getSerializer(EntityId metaDataId);

	public CollectionSerializer<?> getCollectionSerializer(EntityId metaDataId);

}
