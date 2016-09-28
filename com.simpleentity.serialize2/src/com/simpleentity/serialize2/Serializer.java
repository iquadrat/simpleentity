package com.simpleentity.serialize2;

import com.simpleentity.serialize2.generic.ObjectInfo;

public interface Serializer<T> {

	public ObjectInfo serialize(T object);

	public T deserialize(ObjectInfo objectInfo);

	public Class<T> getType();

//	public EntityId getMetaDataId();

}
