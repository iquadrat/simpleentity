package com.simpleentity.serialize2.binary;

import org.povworld.collection.common.MathUtil;
import org.povworld.collection.immutable.ImmutableArrayList;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.BinarySerializer;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.GenericValue.CollectionValue;
import com.simpleentity.serialize2.generic.GenericValue.EntityIdValue;
import com.simpleentity.serialize2.generic.GenericValue.PrimitiveValue;
import com.simpleentity.serialize2.generic.GenericValue.ValueObjectValue;
import com.simpleentity.serialize2.generic.GenericValue.ValueVisitor;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.Cardinality;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaType;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

class EntrySerializer {
	private final String entryId;
	private final Cardinality cardinality;
	private final MetaData declaredType;
	private final SerializerRepository serializerRepository;

	EntrySerializer(String fieldId, Cardinality cardinality, MetaData declaredType,
			SerializerRepository serializerRepository) {
		this.entryId = fieldId;
		this.cardinality = cardinality;
		this.declaredType = declaredType;
		this.serializerRepository = serializerRepository;
	}

	void serialize(ObjectInfo objectInfo, ByteWriter destination) {
		if (cardinality.getMax() > 1) {
			serialize(declaredType.getMetaType(), objectInfo.getCollectionValue(entryId), destination);
		} else {
			GenericValue value = objectInfo.getValue(entryId);
			if (!declaredType.getMetaType().isPolymorphic() && (cardinality.getMin() == 0)) {
				// As we do not serialize the type if the declared type is not
				// polymorphic we need to serialize whether it is present or not.
				if (value == null) {
					destination.putVarInt(0); // TODO optimize
					return;
				}
				destination.putVarInt(1); // TODO optimize
			}
			serialize(declaredType.getMetaType(), value, destination);
		}
	}

	private void serialize(@CheckForNull MetaType metaType, @CheckForNull GenericValue value, final ByteWriter destination) {
		if (value == null) {
			serializerRepository.getEntityIdSerializer().serialize(BootStrap.ID_NULL_REFERENCE, destination);
			return;
		}

		// TODO check that type matches declared type
		// TODO replace ValueVisitor by switch over MetaType for symmetry with
		// deserialize?
		// Or replace MetaData with subclasses (probably better).
		value.accept(new ValueVisitor() {
			@Override
			public void visit(PrimitiveValue primitive) {
				// TODO get from serializerRepository?
				// TODO oops that's ugly
				@SuppressWarnings("unchecked")
				BinarySerializer<Object> serializer = (BinarySerializer<Object>) serializerRepository
						.getPrimitiveSerializer(primitive.getActualMetaDataId());
				serializer.serialize(primitive.getValue(), destination);
			}

			@Override
			public void visit(ValueObjectValue valueObject) {
				EntityId metaTypeId = valueObject.getActualMetaDataId();
				serializerRepository.getEntityIdSerializer().serialize(metaTypeId, destination);
				BinarySerializer<ObjectInfo> serializer = serializerRepository.getBinarySerializer(metaTypeId);
				serializer.serialize(valueObject.getValue(), destination);
			}

			@Override
			public void visit(EntityIdValue entityReference) {
				serializerRepository.getEntityIdSerializer().serialize(entityReference.getEntityId(), destination);
			}

			@Override
			public void visit(CollectionValue collection) {
				// Write collection type id.
				EntityId typeId = collection.getCollectionInfo().getMetaTypeId();
				serializerRepository.getEntityIdSerializer().serialize(typeId, destination);
				// Write custom collection implementation data.
				BinarySerializer<ObjectInfo> serializer = serializerRepository.getBinarySerializer(typeId);
				serializer.serialize(collection.getCollectionInfo(), destination);
				// Write element count.
				destination.putVarInt(collection.count());
				// Write all elements.
				for (GenericValue element : collection.getValues()) {
					serialize(null, element, destination);
				}
			}
		});
	}

	void deserialize(ByteReader source, ObjectInfo.Builder objectInfoBuilder) {
		GenericValue value = deserializeValue(source);
		if (value != null) {
			objectInfoBuilder.setEntryValue(entryId, value);
		}
	}

	private GenericValue deserializeValue(ByteReader source) {
		if (cardinality.getMax() > 1) {
			return deserializeValue(source, declaredType);
		} else {
			if (!declaredType.getMetaType().isPolymorphic() && (cardinality.getMin() == 0)) {
				// As we do not serialize the type if the declared type is not
				// polymorphic
				// we need to serialize whether it is present or not.
				if (source.getVarInt() != 0) {
					return null;
				}
			}
			return deserializeValue(source, declaredType);
		}
	}

	@CheckForNull
	private GenericValue deserializeValue(ByteReader source, @CheckForNull MetaData declaredType) {
		MetaData actualType;
		if (declaredType != null && !declaredType.getMetaType().isPolymorphic()) {
			actualType = declaredType;
		} else {
			EntityId actualTypeId = serializerRepository.getEntityIdSerializer().deserialize(source);
			actualType = serializerRepository.getMetaData(actualTypeId);
		}

		switch (actualType.getMetaType()) {
		case ENTITY:
			return new EntityIdValue(serializerRepository.getEntityIdSerializer().deserialize(source));
		case PRIMITIVE: {
			Primitive primitive = Primitive.byEntityId(actualType.getEntityId());
			BinarySerializer<?> serializer = serializerRepository.getPrimitiveSerializer(actualType.getEntityId());
			return new PrimitiveValue(primitive, serializer.deserialize(source));
		}
		case VALUE_OBJECT: {
			BinarySerializer<ObjectInfo> serializer = serializerRepository.getBinarySerializer(actualType.getEntityId());
			return new ValueObjectValue(serializer.deserialize(source));
		}
		case COLLECTION: {
			// Read custom collection implementation data.
			BinarySerializer<ObjectInfo> serializer = serializerRepository
					.getBinarySerializer(actualType.getElementTypeId());
			ObjectInfo collectionInfo = serializer.deserialize(source);
			// Read element count.
			int count = MathUtil.longToInt(source.getVarInt());
			// Write all elements.
			ImmutableArrayList.Builder<GenericValue> builder = ImmutableArrayList.newBuilder(count);
			for (int i = 0; i < count; ++i) {
				builder.add(deserializeValue(source, null));
			}
			return new CollectionValue(collectionInfo, builder.build());
		}
		default:
			throw new IllegalStateException("Unknown MetaType " + actualType.getMetaType());
		}
	}

}
