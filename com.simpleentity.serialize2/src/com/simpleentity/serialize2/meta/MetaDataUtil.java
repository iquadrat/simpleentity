package com.simpleentity.serialize2.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.povworld.collection.common.PreConditions;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.meta.MetaData.Builder;
import com.simpleentity.util.TypeUtil;

// TODO rename to ReflectUtil
public class MetaDataUtil {

	public static MetaData getMetaDataByReflection(Class<?> class_, String domain, long version, MetaType metaType,
			EntityId id, SerializerRepository serializerRepository) {
		// Build a first version of MetaData already to reserve the id. This is
		// important in case the fields reference the type that is just being
		// created. TODO this should be obsolete now
		MetaData nakedMetaData = MetaData.newBuilder()
				.setClassName(class_.getName())
				.setDomain(domain)
				.setVersion(version)
				.setMetaType(metaType)
				.build(id);
		// TODO elementTypeId is never set
		Builder builder = nakedMetaData.toBuilder();
		addFieldsToMetaDataEntries(class_, serializerRepository, builder);
		return builder.build(id);
	}

	public static void addFieldsToMetaDataEntries(Class<?> class_, SerializerRepository serializerRepository,
			Builder builder) {
		for (Field field : TypeUtil.getAllFields(class_)) {
			Type declaredType = serializerRepository.getDeclaredType(field);
			builder.addEntry(getId(field), declaredType);
		}
	}

	public static String getId(Field field) {
		return field.getName();
	}

	public static boolean isOptional(Field field) {
		for(Annotation annotation: field.getAnnotations()) {
			if (annotation.annotationType().getSimpleName().equals("CheckForNull")) {
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
