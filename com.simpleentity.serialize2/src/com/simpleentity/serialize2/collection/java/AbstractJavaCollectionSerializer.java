package com.simpleentity.serialize2.collection.java;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;

import org.povworld.collection.common.ObjectUtil;

import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.collection.CollectionSerializer;

public abstract class AbstractJavaCollectionSerializer<C extends Collection<Object>> implements CollectionSerializer<C> {

	private final Class<C> type;

	protected AbstractJavaCollectionSerializer(Class<C> type) {
		this.type = type;
	}

	/**
	 * This implementation assumes that the type is {@code SomeCollection<E>}
	 * where {@code E} is the single parameter and corresponds to the element
	 * type.
	 */
	protected Class<?> getElementMetaDataId(Field field, SerializerRepository serializerRepository) {
		if (!type.isAssignableFrom(field.getType())) {
			throw new IllegalArgumentException("Field type " + field.getType().getName() + " is not compatible with "
					+ type.getName());
		}
		ParameterizedType genType = ObjectUtil.castOrNull(field.getGenericType(), ParameterizedType.class);
		if (genType == null) {
			throw new SerializerException("Field type " + field.getGenericType() + " is not a ParametrizedType!");
		}
		java.lang.reflect.Type[] typeArguments = genType.getActualTypeArguments();
		if (typeArguments.length != 1) {
			throw new SerializerException("Expected exactly one type argument but got: "
					+ Arrays.toString(typeArguments));
		}
		return (Class<?>)typeArguments[0];
	}

}
