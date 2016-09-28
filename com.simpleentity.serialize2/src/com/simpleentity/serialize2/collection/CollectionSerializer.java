package com.simpleentity.serialize2.collection;

import org.povworld.collection.Collection;

import com.simpleentity.serialize2.Serializer;

public interface CollectionSerializer<C> extends Serializer<C> {

	public void fill(C collection, Iterable<?> elements);

	public Collection<?> asCollection(C collection);

}
