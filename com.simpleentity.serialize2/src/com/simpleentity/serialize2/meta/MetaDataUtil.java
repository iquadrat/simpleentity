package com.simpleentity.serialize2.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.povworld.collection.CollectionUtil;
import org.povworld.collection.common.PreConditions;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.annotation.ValueObject;
import com.simpleentity.entity.Entity;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.meta.MetaData.Builder;
import com.simpleentity.util.TypeUtil;

// TODO rename to ReflectUtil and move to .reflect?
public class MetaDataUtil {

	public static MetaData getMetaDataByReflection(Class<?> type, String domain, long version, EntityId id,
			MetaDataRepository metaDataRepository) {
		MetaType metaType = tryGetMetaType(type);
		if (metaType == null) {
			// TODO fail if strict type model is enabled
			metaType = MetaType.VALUE_OBJECT;
		}

		Builder builder = MetaData.newBuilder()
				.setDomain(domain)
				.setVersion(version)
				.setMetaType(metaType)
				.setClassName(type.getName());
		switch (metaType) {
		case ENTITY:
		case VALUE_OBJECT:
			addFieldsToMetaDataEntries(type, metaDataRepository, builder);
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

	@CheckForNull
	public static MetaType tryGetMetaType(Class<?> type) {
		if (TypeUtil.hasAnnotation(type, ValueObject.class)) {
			return MetaType.VALUE_OBJECT;
		}
		if (Entity.class.isAssignableFrom(type)) {
			return MetaType.ENTITY;
		}
		if (type.isEnum()) {
			return MetaType.ENUM;
		}
		if (type.isPrimitive()) {
			return MetaType.PRIMITIVE;
		}
		// Cannot decide on the MetaType just from class.
		return null;
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

	public static boolean deepEquals(MetaData metaData1, MetaData metaData2) {
		return metaData1.equals(metaData2)
			&& metaData1.getClassName().equals(metaData2.getClassName())
			&& metaData1.getDomain().equals(metaData2.getDomain())
			&& CollectionUtil.iteratesEqualSequence(metaData1.getEntries(), metaData2.getEntries())
			&& (metaData1.getMetaType() == metaData2.getMetaType())
			&& (metaData1.getVersion() == metaData2.getVersion());
	}

}
