package com.simpleentity.serialize2.reflect;

import java.lang.reflect.Field;

import org.povworld.collection.common.MathUtil;
import org.povworld.collection.common.PreConditions;
import org.povworld.collection.immutable.ImmutableArrayList;
import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.serialize2.AbstractSerializer;
import com.simpleentity.serialize2.Instantiator;
import com.simpleentity.serialize2.SerializationContext;
import com.simpleentity.serialize2.SerializationUtil;
import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.generic.GenericValue.PrimitiveValue;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.Entry;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaType;
import com.simpleentity.serialize2.meta.Type;

public class ReflectiveSerializer<T> extends AbstractSerializer<T> {

	private final Instantiator instantiator;
	private final MetaData metaData;
	private final ImmutableList<FieldSerializer<T>> fieldSerializers;

	public ReflectiveSerializer(Class<T> type, MetaData metaData, SerializationContext context) {
		super(type);
		PreConditions.paramCheck(type, "Enum MetaData must have enum type!",
				metaData.getMetaType() != MetaType.ENUM || type.isEnum());
		this.metaData = metaData;
		this.instantiator = context.getInstantiator();
		this.fieldSerializers = createFieldSerializers(context, type, metaData);
	}

	private static <T> ImmutableList<FieldSerializer<T>> createFieldSerializers(SerializationContext context,
			Class<T> entityClass, MetaData metaData) {
		ImmutableArrayList.Builder<FieldSerializer<T>> result = ImmutableArrayList.newBuilder();
		for(Entry entry: metaData.getEntries()) {
			result.add(createFieldSerializer(context, entityClass, entry.getId(), entry.getType()));
		}
		return result.build();
	}

	private static <T> FieldSerializer<T> createFieldSerializer(SerializationContext context, Class<T> entityClass,
			String id, Type type) {
		Field field = SerializationUtil.findField(entityClass, id);
		field.setAccessible(true);
		ValueSerializer valueSerializer = new ValueSerializer(context, type.getMetaDataId());
		return new FieldSerializer<>(id, field, valueSerializer);
	}

	@Override
	public ObjectInfo serialize(T entity) {
		// TODO make PreConditions better
		PreConditions.paramCheck(entity, "Class mismatch!", entity.getClass().equals(getType()));
		ObjectInfo.Builder builder = ObjectInfo.newBuilder();
		builder.setMetaDataId(metaData.getEntityId());
		for (FieldSerializer<T> fieldSerializer : fieldSerializers) {
			fieldSerializer.serialize(entity, builder);
		}
		return builder.build();
	}

	@Override
	public T deserialize(ObjectInfo objectInfo) {
		// TODO make a custom class EnumSerializer that stores the enum array?
		if (metaData.getMetaType() == MetaType.ENUM) {
			return deserializeEnum(objectInfo);
		}
		T entity = instantiator.newInstance(getType());
		for (FieldSerializer<T> fieldSerializer : fieldSerializers) {
			fieldSerializer.deserialize(objectInfo, entity);
		}
		return entity;
	}

	private T deserializeEnum(ObjectInfo objectInfo) {
		PrimitiveValue ordinalValue = objectInfo.getPrimitiveValue(BootStrap.ENUM_ORDINAL);
		// TODO add some utility for getting primitives from ObjectInfo in the correct type.
		if (ordinalValue == null) {
			throw new SerializerException("Required entry '"+BootStrap.ENUM_ORDINAL+"' missing!");
		}
		int ordinal = MathUtil.longToInt((long)ordinalValue.getValue());
		return getType().getEnumConstants()[ordinal];
	}

}
