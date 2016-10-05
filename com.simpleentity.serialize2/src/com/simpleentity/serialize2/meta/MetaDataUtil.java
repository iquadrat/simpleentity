package com.simpleentity.serialize2.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.povworld.collection.common.PreConditions;

import com.simpleentity.annotation.ValueObject;
import com.simpleentity.entity.Entity;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.meta.MetaData.Builder;
import com.simpleentity.util.TypeUtil;

// TODO rename to ReflectUtil and move to .reflect?
public class MetaDataUtil {

	public static MetaData getMetaDataByReflection(Class<?> type, String domain, long version, EntityId id,
			MetaDataRepository repository) {

		MetaType metaType = getMetaType(type);

		// Build a first version of MetaData already to reserve the id. This is
		// important in case the fields reference the type that is just being
		// created. TODO this should be obsolete now
		MetaData nakedMetaData = MetaData.newBuilder().setClassName(type.getName()).setDomain(domain)
				.setVersion(version).setMetaType(metaType).build(id);
		Builder builder = nakedMetaData.toBuilder();
		switch (metaType) {
		case ENTITY:
		case VALUE_OBJECT:
			addFieldsToMetaDataEntries(type, repository, builder);
			break;
		case ENUM:
			addEnumFieldsToMetaDataEntries(type, builder);
			break;
		case COLLECTION:
		case PRIMITIVE:
		case UNKNOWN:
			throw new IllegalArgumentException("Cannot handle " + metaType);
		default:
			break;
		}

		return builder.build(id);
	}

	public static MetaType getMetaType(Class<?> type) {
		if (TypeUtil.hasAnnotation(type, ValueObject.class)) {
			return MetaType.VALUE_OBJECT;
		}
		if (Entity.class.isAssignableFrom(type)) {
			return MetaType.ENTITY;
		}
		if (type.isEnum()) {
			return MetaType.ENUM;
		}
		// TODO fail if strict type checking is enabled
		return MetaType.VALUE_OBJECT;
	}

	public static void addEnumFieldsToMetaDataEntries(Class<?> enumType, Builder builder) {
		PreConditions.paramCheck(enumType, "Not an Enum!", enumType.isEnum());
		// TODO store list of enum constants here? This might help with
		// displaying ObjectInfo and refactorings.
		//builder.addEntry(BootStrap.ENUM_ORDINAL, new Type(BootStrap.ID_PRIMITIVE_VARINT, false));
	}

	public static void addFieldsToMetaDataEntries(Class<?> class_, MetaDataRepository metaDataRepository,
			Builder builder) {
		for (Field field : TypeUtil.getAllFields(class_)) {
			Type declaredType = metaDataRepository.getDeclaredType(field);
			builder.addEntry(getId(field), declaredType);
		}
	}

	public static String getId(Field field) {
		return field.getName();
	}

	public static boolean isOptional(Field field) {
		for (Annotation annotation : field.getAnnotations()) {
			if (annotation.annotationType().getSimpleName().equals("CheckForNull")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPositive(Field field) {
		for (Annotation annotation : field.getAnnotations()) {
			if (annotation.annotationType().getSimpleName().equals("Positive")) {
				return true;
			}
		}
		return false;
	}

	public static EntityId getArrayMetaDataId(Class<?> arrayType) {
		PreConditions.paramCheck(arrayType, "Not an array type!", arrayType.isArray());
		Class<?> componentType = arrayType.getComponentType();
		if (componentType.isArray()) {
			return BootStrap.ID_MULTI_DIMENSIONAL_ARRAY;
		}
		if (componentType.isPrimitive()) {
			return BootStrap.ID_PRIMITIVE_ARRAY;
		} else {
			return BootStrap.ID_OBJECT_ARRAY;
		}
	}


}
