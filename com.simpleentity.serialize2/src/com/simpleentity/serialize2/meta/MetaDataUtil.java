package com.simpleentity.serialize2.meta;

import java.lang.reflect.Field;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.id.EntityIdFactory;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.meta.MetaData.Builder;
import com.simpleentity.util.ClassUtil;

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
		for (Field field : ClassUtil.getAllFields(class_)) {
			String id = getId(field);
			MetaData metaData = serializerRepository.getMetaData(field.getType());
			EntityId cardinality;
			if (metaData.getMetaType() == MetaType.COLLECTION) {
				cardinality = BootStrap.ID_CARDINALITY_ANY;
			} else {
				cardinality = isOptional(field) ? BootStrap.ID_CARDINALITY_OPTIONAL : BootStrap.ID_CARDINALITY_ONE;
			}
			builder.addEntry(new Entry(id, cardinality, metaData.getEntityId()));
		}
	}

	private static boolean isOptional(Field field) {
		// TODO
		return false;
	}

	public static String getId(Field field) {
		return field.getName();
	}

}
