package com.simpleentity.serialize2.meta;

import java.lang.reflect.Field;

import com.simpleentity.entity.id.EntityId;

public interface MetaDataRepository {
	public MetaData getMetaData(EntityId metaDataId);

	// TODO Result is not unique! Pass in type annotations?
	public EntityId getMetaDataId(Class<?> class_);

	// TODO Result is not unique! Pass in type annotations?
	public MetaData getMetaData(Class<?> class_);

	public Type getDeclaredType(Field field);
}
