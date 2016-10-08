package com.simpleentity.serialize2.collection;

import java.lang.reflect.Field;

import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.Type;


public interface CollectionSerializer<C> {

	public MetaData getMetaData();

	public CollectionInfo serialize(C collection);

	public C deserialize(CollectionInfo collectionInfo);

	public Type getElementType(Field field);

}
