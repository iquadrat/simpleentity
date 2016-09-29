package com.simpleentity.serialize2.java;

import java.lang.reflect.Field;

import org.povworld.collection.common.PreConditions;
import org.povworld.collection.immutable.ImmutableArrayList;
import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.AbstractSerializer;
import com.simpleentity.serialize2.SerializationUtil;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.Cardinality;
import com.simpleentity.serialize2.meta.Entry;
import com.simpleentity.serialize2.meta.MetaData;

public class JavaSerializer<T> extends AbstractSerializer<T> {

	// private static final Field idField;
	// static {
	// try {
	// idField = Entity.class.getDeclaredField("entityId");
	// } catch (NoSuchFieldException | SecurityException e) {
	// throw new RuntimeException(e);
	// }
	// }

	private final JavaSerializationContext context;
	private final MetaData metaData;

//	private final Class<T> entityClass;
	private final ImmutableList<FieldSerializer<T>> fieldSerializers;

	public JavaSerializer(JavaSerializationContext context, Class<T> type) {
		super(type);
		this.context = context;
		this.metaData = context.getSerializerRepository().getMetaData(type);
		//PreConditions.paramCheck(metaData, "Must be of meta type ENTITY", metaData.getMetaType() == MetaType.ENTITY);
//		try {
//			@SuppressWarnings("unchecked")
//			Class<T> entityClass_ = (Class<T>) context.getClassLoader().loadClass(metaData.getClassName());
//			entityClass = entityClass_;
//			// TODO do sanity checks on class.
//		} catch (ClassNotFoundException e) {
//			throw new SerializerException(e);
//		}
		// this.newBuilderMethod =
		// SerializationUtil.getNewBuilderMethod(entityClass);
		// this.buildMethod =
		// SerializationUtil.getEntityBuilderBuildMethod(builderClass);
		this.fieldSerializers = createFieldSerializers(context, type, metaData);
	}

	private static <T> ImmutableList<FieldSerializer<T>> createFieldSerializers(
			JavaSerializationContext context, Class<T> entityClass, MetaData metaData) {
		ImmutableArrayList.Builder<FieldSerializer<T>> result = ImmutableArrayList.newBuilder();
		for (Entry entry : metaData.getEntries()) {
			result.add(createFieldSerializer(context, entityClass, entry));
		}
		return result.build();
	}

	private static <T> FieldSerializer<T> createFieldSerializer(JavaSerializationContext context,
			Class<T> entityClass, Entry entry) {
		Field field = SerializationUtil.findField(entityClass, entry.getId());
		field.setAccessible(true);
		Cardinality cardinality = context.getSerializerRepository().getCardinality(entry.getCardinality());
		ValueSerializer valueSerializer = getValueSerializer(context, entry.getDeclaredTypeId(), cardinality);
		return new FieldSerializer<>(entry.getId(), field, valueSerializer);
	}

	private static ValueSerializer getValueSerializer(JavaSerializationContext context, EntityId declaredTypeId,
			Cardinality cardinality) {
		return new ValueSerializer(context.getSerializerRepository());
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
		T entity = context.getInstantiator().newInstance(getType());
		for (FieldSerializer<T> fieldSerializer : fieldSerializers) {
			fieldSerializer.deserialize(objectInfo, entity);
		}
		return entity;
	}

}
