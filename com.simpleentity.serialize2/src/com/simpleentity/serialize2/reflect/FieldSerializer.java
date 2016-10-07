package com.simpleentity.serialize2.reflect;

import java.lang.reflect.Field;

import javax.annotation.CheckForNull;

import org.povworld.collection.UnOrderedSet;
import org.povworld.collection.common.ObjectUtil;
import org.povworld.collection.immutable.ImmutableCollections;

import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.Primitive;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

// TODO merge with ValueSerializer?
class FieldSerializer<T> {
	private final String entryId;
	private final Field field;
	private final ValueSerializer valueSerializer;

	public FieldSerializer(String entryId, Field field, ValueSerializer valueSerializer) {
		this.entryId = entryId;
		this.field = field;
		this.valueSerializer = valueSerializer;
	}

	public void serialize(T entity, ObjectInfo.Builder builder) {
		Object value = getRawOrNull(entity);
		if (value == null) {
			return;
		}
		builder.setEntryValue(entryId, valueSerializer.serialize(value));
	}

	public void deserialize(ObjectInfo objectInfo, T entity) {
		GenericValue value = objectInfo.getValue(entryId);
		if (value == null) {
			return;
		}
		setRaw(entity, valueSerializer.deserialize(value));
	}

	@CheckForNull
	private Object getRawOrNull(T entity) {
		try {
			return field.get(entity);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new SerializerException(e);
		}
	}

	private void setRaw(T entity, Object value) {
		try {
			field.set(entity, convert(value, field.getType()));
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new SerializerException(e);
		}
	}

	private static final UnOrderedSet<Class<?>> integerClasses = ImmutableCollections.<Class<?>> asUnOrderedSet(
			Short.class, Integer.class, Long.class);

	private Object convert(Object value, Class<?> type) {
		if (type.isPrimitive()) {
			type = Primitive.byType(type).getBoxedType();
		}

		if (type.isInstance(value) || type.isPrimitive()) {
			return value;
		}
		if (Number.class.isAssignableFrom(type) && integerClasses.contains(value.getClass())) {
			Number number = ObjectUtil.castOrNull(value, Number.class);
				long longValue = number.longValue();
				if (Short.class.equals(type) && longValue <= Short.MAX_VALUE && longValue >= Short.MIN_VALUE) {
					return Short.valueOf(number.shortValue());
				}
				if (Integer.class.equals(type) && longValue <= Integer.MAX_VALUE && longValue >= Integer.MIN_VALUE) {
					return Integer.valueOf(number.intValue());
				}
				if (Long.class.equals(type)) {
					return Long.valueOf(longValue);
				}
		}
		if (Double.class == type && (value instanceof Float)) {
			return Double.valueOf((float)value);
		}
		throw new SerializationException("Type missmatch! Cannot assign '"+value+"' which is of type "+value.getClass().getName()+" to field of type "+type);
	}
}
