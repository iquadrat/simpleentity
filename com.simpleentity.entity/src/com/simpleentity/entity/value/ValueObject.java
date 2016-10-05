package com.simpleentity.entity.value;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.povworld.collection.common.ObjectUtil;

import com.simpleentity.util.TypeUtil;

@com.simpleentity.annotation.ValueObject
public abstract class ValueObject {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || object.getClass() != getClass()) {
			return false;
		}
		try {
			for (Field field : TypeUtil.getAllFields(getClass())) {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				if (!ObjectUtil.objectEquals(field.get(this), field.get(object))) {
					return false;
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public int hashCode() {
		try {
			int hashCode = 1;
			for (Field field : TypeUtil.getAllFields(getClass())) {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				Object value = field.get(this);
				hashCode = 31 * hashCode + ((value == null) ? 27 : value.hashCode());
			}
			return hashCode;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		Iterator<Field> iterator = TypeUtil.getAllFields(getClass()).iterator();
		try {
			while (iterator.hasNext()) {
				Field field = iterator.next();
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				Object value = field.get(this);
				sb.append(field.getName() + "=" + value);
				if (iterator.hasNext()) {
					sb.append(", ");
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		sb.append("]");
		return sb.toString();
	}

}
