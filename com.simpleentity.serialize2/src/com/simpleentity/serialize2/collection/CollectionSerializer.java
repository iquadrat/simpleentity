package com.simpleentity.serialize2.collection;

import java.lang.reflect.Field;

import org.povworld.collection.Collection;

import com.simpleentity.serialize2.Serializer;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.meta.Type;

public interface CollectionSerializer<C> extends Serializer<C> {

	public void fill(C collection, Iterable<?> elements);

	public Collection<?> asCollection(C collection);

	public Type getElementType(Field field, SerializerRepository serializerRepository);

}
