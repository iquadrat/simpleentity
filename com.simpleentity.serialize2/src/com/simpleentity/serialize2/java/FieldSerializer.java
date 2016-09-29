package com.simpleentity.serialize2.java;

import java.lang.reflect.Field;

import javax.annotation.CheckForNull;

import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.ObjectInfo;

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
			field.set(entity, value);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new SerializerException(e);
		}
	}
}
