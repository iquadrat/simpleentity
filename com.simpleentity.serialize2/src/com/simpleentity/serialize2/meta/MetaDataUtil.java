package com.simpleentity.serialize2.meta;

import java.lang.reflect.Field;

import com.simpleentity.entity.id.EntityIdFactory;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.meta.MetaData.Builder;
import com.simpleentity.util.TypeUtil;

// TODO rename to ReflectUtil
public class MetaDataUtil {

	public static MetaData getMetaDataByReflection(Class<?> class_, String domain, long version, MetaType metaType,
			EntityIdFactory idFactory, SerializerRepository serializerRepository) {
		// Build a first version of MetaData already to reserve the id. This is
		// important in case the fields reference the type that is just being
		// created.
		MetaData nakedMetaData = MetaData.newBuilder().setClassName(class_.getName()).setDomain(domain)
				.setVersion(version).setMetaType(metaType).build(idFactory);
		// TODO elementTypeId is never set
		Builder builder = nakedMetaData.toBuilder();
		addFieldsToMetaDataEntries(class_, serializerRepository, builder);
		return builder.build(idFactory);
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

//
//	public static Type getDeclaredType(Field field, SerializerRepository serializerRepository) {
//		serializerRepository.getMetaData(class_)
//
//		return getDeclaredType(field.getGenericType(), isOptional(field), serializerRepository);
//	}
//
//
//	private static Type getDeclaredType(java.lang.reflect.Type type, boolean optional, SerializerRepository serializerRepository) {
//		Class<?> class_ = ObjectUtil.castOrNull(type, Class.class);
//		if (class_ != null) {
//			return new Type(serializerRepository.getMetaDataId(class_), optional);
//		}
//
//
//		ParameterizedType parameterizedType = ObjectUtil.castOrNull(type, ParameterizedType.class);
//		parameterizedType.getActualTypeArguments();
//		class_ = (Class<?>)parameterizedType.getRawType();
//		MetaData metaData = serializerRepository.getMetaData(class_);
//		if (metaData.getMetaType() == MetaType.COLLECTION) {
//			CollectionSerializer<C>
//		} else {
//			return new Type(serializerRepository.getMetaDataId(class_), optional);
//		}
//	}
//
//	public static boolean isOptional(Field field) {
//		for(Annotation annotation: field.getAnnotations()) {
//			if (annotation.annotationType().getSimpleName().equals("CheckForNull")) {
//				return true;
//			}
//		}
//		return false;
//	}

}
