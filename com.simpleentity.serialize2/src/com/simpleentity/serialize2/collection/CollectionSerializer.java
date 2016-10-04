package com.simpleentity.serialize2.collection;


public interface CollectionSerializer<C> {

	public CollectionInfo serialize(C collection);

	public C deserialize(CollectionInfo collectionInfo);

}
