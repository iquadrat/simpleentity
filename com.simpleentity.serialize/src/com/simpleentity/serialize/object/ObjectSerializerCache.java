/*
 * Created on Dec 1, 2007
 *
 */
package com.simpleentity.serialize.object;

import java.util.*;

import com.simpleentity.serialize.IObjectSerializerRegistry;

/**
 * Cache which remembers and reuses object serializer created by a base
 * serializer.
 *
 * @author micha
 *
 */
public class ObjectSerializerCache implements IObjectSerializerFactory, IObjectSerializerRegistry {

	private final Map<String, IObjectSerializer<?>> fObjectSerializerMap = new HashMap<String, IObjectSerializer<?>>();

	private final IObjectSerializerFactory fFactory;

	public ObjectSerializerCache(IObjectSerializerFactory factory) {
		fFactory = factory;
	}

	@Override
	public <T> void registerSerializer(Class<T> clazz, IObjectSerializer<? super T> serializer) {
		registerSerializer(clazz.getCanonicalName(), serializer);
	}

	@Override
	public <T> void registerSerializer(String className, IObjectSerializer<? super T> serializer) {
		fObjectSerializerMap.put(className, serializer);
	}

	@Override
	public IObjectSerializer<?> getSerializer(Class<?> clazz) {
		IObjectSerializer<?> serializer = fObjectSerializerMap.get(clazz.getCanonicalName());
		if (serializer == null) {
			serializer = fFactory.getSerializer(clazz);
			fObjectSerializerMap.put(clazz.getCanonicalName(), serializer);
		}
		return serializer;
	}

}
