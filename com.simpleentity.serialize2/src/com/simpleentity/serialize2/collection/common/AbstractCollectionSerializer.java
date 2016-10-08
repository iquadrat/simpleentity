package com.simpleentity.serialize2.collection.common;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

import org.povworld.collection.Collection;
import org.povworld.collection.common.ObjectUtil;

import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.collection.CollectionInfo;
import com.simpleentity.serialize2.collection.CollectionSerializer;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaType;

public abstract class AbstractCollectionSerializer<C extends Collection<?>> implements CollectionSerializer<C> {

	private static final String DOMAIN = "org.povworld.collection";

	private final Class<?> type;
	private final MetaData metaData;

	protected static MetaData.Builder newMetaDataBuilder() {
		return MetaData.newBuilder()
				.setDomain(DOMAIN)
				.setVersion(0)
				.setMetaType(MetaType.COLLECTION);
	}

	protected AbstractCollectionSerializer(Class<?> type, MetaData metaData) {
		this.type = type;
		this.metaData = metaData;
	}

	@Override
	public MetaData getMetaData() {
		return metaData;
	}

	@Override
	public CollectionInfo serialize(C collection) {
		return new CollectionInfo(getObjectInfo(collection), BootStrap.ID_ANY, collection);
	}

	protected ObjectInfo getObjectInfo(C collection) {
		ObjectInfo.Builder builder = ObjectInfo.newBuilder()
				.setMetaDataId(metaData.getEntityId());
		addObjectInfoEntries(collection, builder);
		return builder.build();
	}

	protected void addObjectInfoEntries(C collection, ObjectInfo.Builder builder) {
	}

	/**
	 * This implementation assumes that the type is {@code SomeCollection<E>}
	 * where {@code E} is the single parameter and corresponds to the element
	 * type.
	 */
	protected Class<?> getElementMetaDataId(Field field) {
		if (!field.getType().isAssignableFrom(type)) {
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
		WildcardType wildCardType = ObjectUtil.castOrNull(typeArguments[0], WildcardType.class);
		if (wildCardType != null) {
			Type[] upperBounds = wildCardType.getUpperBounds();
			if (upperBounds.length != 1) {
				throw new SerializerException("Unsupported WildcardType '" + wildCardType
						+ "'! Only wild cards of form '? extends Foo' are allowed.");
			}
			return getClass(upperBounds[0]);
		}
		TypeVariable<?> typeVariable = ObjectUtil.castOrNull(typeArguments[0], TypeVariable.class);
		if (typeVariable != null) {
			Type[] bounds = typeVariable.getBounds();
			if (bounds.length != 1) {
				throw new SerializerException("Unsupported TypeVariable '" + typeVariable
						+ "'! Only type variable of form '? extends Foo' are allowed.");
			}
			return getClass(bounds[0]);
		}
		return getClass(typeArguments[0]);
	}

	private Class<?> getClass(Type type) {
		Class<?> class_ = ObjectUtil.castOrNull(type, Class.class);
		if (class_ != null) {
			return class_;
		}
		ParameterizedType paramType = ObjectUtil.castOrNull(type, ParameterizedType.class);
		if (paramType != null) {
			return getClass(paramType.getRawType());
		}
		throw new IllegalArgumentException(type.toString());
	}

}
