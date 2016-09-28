package com.simpleentity.serialize2.entity;

import java.lang.reflect.Field;

import org.povworld.collection.common.PreConditions;
import org.povworld.collection.immutable.ImmutableArrayList;
import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.entity.Entity;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.SerializationContext;
import com.simpleentity.serialize2.SerializationUtil;
import com.simpleentity.serialize2.Serializer;
import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.Cardinality;
import com.simpleentity.serialize2.meta.Entry;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaType;

public class EntitySerializer<T extends Entity<T>> implements Serializer<T> {

	// private static final Field idField;
	// static {
	// try {
	// idField = Entity.class.getDeclaredField("entityId");
	// } catch (NoSuchFieldException | SecurityException e) {
	// throw new RuntimeException(e);
	// }
	// }

	private final SerializationContext context;
	private final MetaData metaData;

	private final Class<T> entityClass;
	private final ImmutableList<EntrySerializer<T>> fieldSerializers;

	public EntitySerializer(SerializationContext context, MetaData metaData) {
		PreConditions.paramCheck(metaData, "Must be of meta type ENTITY", metaData.getMetaType() == MetaType.ENTITY);
		this.context = context;
		this.metaData = metaData;
		try {
			@SuppressWarnings("unchecked")
			Class<T> entityClass_ = (Class<T>) context.getClassLoader().loadClass(metaData.getClassName());
			entityClass = entityClass_;
			// TODO do sanity checks on class.
		} catch (ClassNotFoundException e) {
			throw new SerializerException(e);
		}
		// this.newBuilderMethod =
		// SerializationUtil.getNewBuilderMethod(entityClass);
		// this.buildMethod =
		// SerializationUtil.getEntityBuilderBuildMethod(builderClass);
		this.fieldSerializers = createFieldSerializers(context, entityClass, metaData);
	}

	private static <T extends Entity<T>> ImmutableList<EntrySerializer<T>> createFieldSerializers(
			SerializationContext context, Class<T> entityClass, MetaData metaData) {
		ImmutableArrayList.Builder<EntrySerializer<T>> result = ImmutableArrayList.newBuilder();
		for (Entry entry : metaData.getEntries()) {
			result.add(createFieldSerializer(context, entityClass, entry));
		}
		return result.build();
	}

	private static <T extends Entity<T>> EntrySerializer<T> createFieldSerializer(SerializationContext context,
			Class<T> entityClass, Entry entry) {
		Field field = SerializationUtil.findField(entityClass, entry.getId());
		Cardinality cardinality = context.getSerializerRepository().getCardinality(entry.getCardinality());
		ValueSerializer valueSerializer = getValueSerializer(context, entry.getDeclaredTypeId(), cardinality);
		return new EntrySerializer<>(entry.getId(), field, valueSerializer);
	}

	private static ValueSerializer getValueSerializer(SerializationContext context, EntityId declaredTypeId,
			Cardinality cardinality) {
		return new GenericValueSerializer(context.getSerializerRepository());
		// if (BootStrap.isPrimitive(declaredTypeId)) {
		// // TODO register full MetaData for primitives as well?
		// Primitive primitive = Primitive.byEntityId(declaredTypeId);
		// return PrimitiveSerializer.getSerializer(primitive);
		// }
		// MetaData metaData =
		// context.getSerializerRepository().getMetaData(declaredTypeId);
		// MetaType type = metaData.getMetaType();
		// switch (type) {
		// case ENTITY:
		// return context.getSerializerRepository().getEntityIdSerializer();
		// case VALUE_OBJECT: {
		// Class<?> class_;
		// try {
		// class_ = context.getClassLoader().loadClass(metaData.getClassName());
		// } catch (ClassNotFoundException e) {
		// throw new SerializerException(e);
		// }
		// return new PolymorphicEntrySerializer<>(class_,
		// context.getSerializerRepository());
		// }
		// default:
		// throw new SerializerException("Unhandled MetaType " + type);
		// }
	}

	@Override
	public ObjectInfo serialize(T entity) {
		// TODO make PreConditions better
		PreConditions.paramCheck(entity, "Class mismatch!", entity.getClass().equals(entityClass));
		ObjectInfo.Builder builder = ObjectInfo.newBuilder();
		builder.setMetaDataId(metaData.getEntityId());
		for (EntrySerializer<T> fieldSerializer : fieldSerializers) {
			fieldSerializer.serialize(entity, builder);
		}
		return builder.build();
	}

	@Override
	public T deserialize(ObjectInfo objectInfo) {
		T entity = context.getInstantiator().newInstance(entityClass);
		for (EntrySerializer<T> fieldSerializer : fieldSerializers) {
			fieldSerializer.deserialize(objectInfo, entity);
		}
		return entity;
	}

	@Override
	public Class<T> getType() {
		return entityClass;
	}

}
