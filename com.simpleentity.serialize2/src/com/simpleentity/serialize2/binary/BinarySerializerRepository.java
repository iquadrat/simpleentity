package com.simpleentity.serialize2.binary;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.MetaData;

public interface BinarySerializerRepository {

	public MetaData getMetaData(EntityId metaDataId);

	public BinarySerializer<Object> getPrimitiveSerializer(EntityId metaDataId);

	public BinarySerializer<EntityId> getEntityIdSerializer();

	public BinarySerializer<ObjectInfo> getBinarySerializer(EntityId metaDataId);

}
