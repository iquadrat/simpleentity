package com.simpleentity.serialize2.collection;


// TODO replace C by object?
public interface CollectionSerializer<C> {

	public CollectionInfo<C> serialize(C collection);

	public C deserialize(CollectionInfo<C> collectionInfo);

}
