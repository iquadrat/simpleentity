package com.simpleentity.serialize2.meta;

import java.lang.reflect.Field;

import com.simpleentity.entity.id.EntityId;

public interface MetaDataRepository {
	public MetaData getMetaData(EntityId metaDataId);

	public EntityId getMetaDataId(Class<?> class_);

	public MetaData getMetaData(Class<?> class_);

	public Type getDeclaredType(Field field);
}
