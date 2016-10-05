package com.simpleentity.serialize2.reflect;

import java.lang.reflect.Field;

import org.povworld.collection.EntryIterator;
import org.povworld.collection.common.PreConditions;
import org.povworld.collection.immutable.ImmutableArrayList;
import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.serialize2.AbstractSerializer;
import com.simpleentity.serialize2.Instantiator;
import com.simpleentity.serialize2.SerializationContext;
import com.simpleentity.serialize2.SerializationUtil;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.Type;

public class ReflectiveSerializer<T> extends AbstractSerializer<T> {

	private final Instantiator instantiator;
	private final MetaData metaData;
	private final ImmutableList<FieldSerializer<T>> fieldSerializers;

	public ReflectiveSerializer(Class<T> type, MetaData metaData, SerializationContext context) {
		super(type);
		this.metaData = metaData;
		this.instantiator = context.getInstantiator();
		this.fieldSerializers = createFieldSerializers(context, type, metaData);
	}

	private static <T> ImmutableList<FieldSerializer<T>> createFieldSerializers(SerializationContext context,
			Class<T> entityClass, MetaData metaData) {
		ImmutableArrayList.Builder<FieldSerializer<T>> result = ImmutableArrayList.newBuilder();
		EntryIterator<String, Type> entries = metaData.getEntries();
		while (entries.next()) {
			result.add(createFieldSerializer(context, entityClass, entries.getCurrentKey(), entries.getCurrentValue()));
		}
		return result.build();
	}

	private static <T> FieldSerializer<T> createFieldSerializer(SerializationContext context, Class<T> entityClass,
			String id, Type type) {
		Field field = SerializationUtil.findField(entityClass, id);
		field.setAccessible(true);
		ValueSerializer valueSerializer = new ValueSerializer(context.getSerializerRepository());
		return new FieldSerializer<>(id, field, valueSerializer);
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
		T entity = instantiator.newInstance(getType());
		for (FieldSerializer<T> fieldSerializer : fieldSerializers) {
			fieldSerializer.deserialize(objectInfo, entity);
		}
		return entity;
	}

}
