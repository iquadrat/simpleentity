package com.simpleentity.serialize2.binary;

import org.povworld.collection.common.MathUtil;
import org.povworld.collection.immutable.ImmutableArrayList;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.BinarySerializer;
import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.GenericValue.CollectionValue;
import com.simpleentity.serialize2.generic.GenericValue.EntityIdValue;
import com.simpleentity.serialize2.generic.GenericValue.PrimitiveValue;
import com.simpleentity.serialize2.generic.GenericValue.ValueObjectValue;
import com.simpleentity.serialize2.generic.GenericValue.ValueVisitor;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.serialize2.meta.Type;
import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

class EntrySerializer {
	private final String entryId;
	private final Type type;
	private final SerializerRepository serializerRepository;
	private final MetaData metaData;

	EntrySerializer(String fieldId, Type type, SerializerRepository serializerRepository) {
		this.entryId = fieldId;
		this.type = type;
		this.metaData = serializerRepository.getMetaData(type.getMetaDataId());
		this.serializerRepository = serializerRepository;
	}

	void serialize(ObjectInfo objectInfo, ByteWriter destination) {
		serialize(type, metaData, objectInfo.getCollectionValue(entryId), destination);
	}

	private void serialize(final @CheckForNull Type declaredType, @CheckForNull MetaData declaredMetaData,
			@CheckForNull GenericValue value, final ByteWriter destination) {
		boolean optional = (declaredType == null || declaredType.isOptional());
		if (value == null && !optional) {
			throw new SerializerException("Non-optional entry found to be null!");
		}
		if (declaredMetaData == null || declaredMetaData.getMetaType().isPolymorphic()) {
			// Polymorphic type: Write actual type or 'null'-type.
			if (value == null) {
				serializerRepository.getEntityIdSerializer().serialize(BootStrap.ID_NULL_REFERENCE, destination);
				return;
			}
			serializerRepository.getEntityIdSerializer().serialize(value.getActualMetaDataId(), destination);
		} else {
			// Non-polymorphic type: Do not write actual type.
			if (optional) {
				// As we do not serialize the type if the declared type is not
				// polymorphic we need to serialize whether it is present or
				// not.
				destination.putVarInt((value == null) ? 0 : 1); // TODO optimize
			}
		}

		// TODO check that type matches declared type
		// TODO replace ValueVisitor by switch over MetaType for symmetry with
		// deserialize?
		// Or replace MetaData with subclasses (probably better).
		value.accept(new ValueVisitor() {
			@Override
			public void visit(PrimitiveValue primitive) {
				// TODO oops that's ugly
				BinarySerializer<Object> serializer = serializerRepository.getPrimitiveSerializer(primitive
						.getActualMetaDataId());
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
				// EntityId typeId =
				// collection.getCollectionInfo().getMetaTypeId();
				// serializerRepository.getEntityIdSerializer().serialize(typeId,
				// destination);
				// Write custom collection implementation data.
				BinarySerializer<ObjectInfo> serializer = serializerRepository.getBinarySerializer(collection
						.getActualMetaDataId());
				serializer.serialize(collection.getCollectionInfo(), destination);
				// Write element count.
				destination.putVarInt(collection.count());
				// Write all elements.
				for (GenericValue element : collection.getValues()) {
					Type elementType = (declaredType == null) ? null : declaredType.getElementType();
					MetaData metaData = (declaredType == null) ? null : serializerRepository.getMetaData(elementType
							.getMetaDataId());
					serialize(elementType, metaData, element, destination);
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
		return deserializeValue(source, type, metaData);
	}

	@CheckForNull
	private GenericValue deserializeValue(ByteReader source, @CheckForNull Type declaredType,
			@CheckForNull MetaData declaredMetaData) {
		boolean optional = (declaredType == null || declaredType.isOptional());

		MetaData actualMetaData = null;
		if (declaredMetaData == null || declaredMetaData.getMetaType().isPolymorphic()) {
			// Polymorphic type: Read actual type or 'null'-type.
			EntityId actualTypeId = serializerRepository.getEntityIdSerializer().deserialize(source);
			if (actualTypeId != BootStrap.ID_NULL_REFERENCE) {
				actualMetaData = serializerRepository.getMetaData(actualTypeId);
			}
		} else {
			// Non-polymorphic type: Do not read actual type.
			if (!optional || source.getVarInt() != 0) {
				actualMetaData = declaredMetaData;
			}
		}

		if (actualMetaData == null) {
			if (!optional) {
				throw new SerializerException("Non-optional entry found to be null!");
			}
			return null;
		}

		switch (actualMetaData.getMetaType()) {
		case ENTITY:
			return new EntityIdValue(serializerRepository.getEntityIdSerializer().deserialize(source));
		case PRIMITIVE: {
			Primitive primitive = Primitive.byEntityId(actualMetaData.getEntityId());
			BinarySerializer<?> serializer = serializerRepository.getPrimitiveSerializer(actualMetaData.getEntityId());
			return new PrimitiveValue(primitive, serializer.deserialize(source));
		}
		case VALUE_OBJECT: {
			BinarySerializer<ObjectInfo> serializer = serializerRepository.getBinarySerializer(actualMetaData
					.getEntityId());
			return new ValueObjectValue(serializer.deserialize(source));
		}
		case COLLECTION: {
			// Read custom collection implementation data.
			BinarySerializer<ObjectInfo> serializer = serializerRepository.getBinarySerializer(actualMetaData
					.getEntityId());
			ObjectInfo collectionInfo = serializer.deserialize(source);
			// Read element count.
			int count = MathUtil.longToInt(source.getVarInt());
			// Read all elements.
			ImmutableArrayList.Builder<GenericValue> builder = ImmutableArrayList.newBuilder(count);
			for (int i = 0; i < count; ++i) {
				Type elementType = (declaredType == null) ? null : declaredType.getElementType();
				MetaData metaData = (declaredType == null) ? null : serializerRepository.getMetaData(elementType
						.getMetaDataId());
				builder.add(deserializeValue(source, elementType, metaData));
			}
			return new CollectionValue(collectionInfo, builder.build());
		}
		default:
			throw new IllegalStateException("Unknown MetaType " + actualMetaData.getMetaType());
		}
	}

}
