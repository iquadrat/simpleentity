package com.simpleentity.serialize2.reflect;

import java.util.Iterator;

import org.povworld.collection.Collection;
import org.povworld.collection.immutable.ImmutableArrayList;
import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.Serializer;
import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.collection.CollectionSerializer;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.GenericValue.CollectionValue;
import com.simpleentity.serialize2.generic.GenericValue.EntityIdValue;
import com.simpleentity.serialize2.generic.GenericValue.PrimitiveValue;
import com.simpleentity.serialize2.generic.GenericValue.ValueObjectValue;
import com.simpleentity.serialize2.generic.GenericValue.ValueVisitor;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.util.Reference;

class ValueSerializer {

	private final SerializerRepository serializerRepository;

	ValueSerializer(SerializerRepository serializerRepository) {
		this.serializerRepository = serializerRepository;
	}

	public GenericValue serialize(Object value) {
		MetaData metaData = serializerRepository.getMetaData(value.getClass());
		// TODO check expected type matches actual
		switch (metaData.getMetaType()) {
		case ENTITY:
			return new EntityIdValue((EntityId) value);
		case PRIMITIVE:
			return new PrimitiveValue(Primitive.byEntityId(metaData.getEntityId()), value);
		case VALUE_OBJECT:
			return serializeValueObject(metaData, serializerRepository.getSerializer(metaData.getEntityId()), value);
		case COLLECTION:
			return serializeCollection(metaData, serializerRepository.getCollectionSerializer(metaData.getEntityId()),
					value);
		default:
			throw new SerializerException("Unknown MetaType " + metaData.getMetaType());
		}
	}

	private <V> ValueObjectValue serializeValueObject(MetaData metaData, Serializer<V> serializer, Object value) {
		return new ValueObjectValue(serializer.serialize(serializer.getType().cast(value)));
	}

	private <C> CollectionValue serializeCollection(MetaData metaData, CollectionSerializer<C> serializer, Object value) {
		C collection = serializer.getType().cast(value);
		ObjectInfo collectionInfo = serializer.serialize(collection);
		if (collectionInfo == null) {
			throw new SerializerException("CollectionSerializer " + serializer + " returned null when serializing "
					+ value);
		}
		Collection<?> adpatedCollection = serializer.asCollection(collection);
		ImmutableArrayList.Builder<GenericValue> elements = ImmutableArrayList.newBuilder(adpatedCollection.size());
		for (Object element : adpatedCollection) {
			elements.add(serialize(element));
		}
		return new CollectionValue(collectionInfo, elements.build());
	}

	public Object deserialize(GenericValue value) {
		// TODO check expected type matches actual
		final Reference<Object> result = new Reference<>();
		value.accept(new ValueVisitor() {
			@Override
			public void visit(PrimitiveValue primitive) {
				result.set(primitive.getValue());
			}

			@Override
			public void visit(EntityIdValue entityReference) {
				result.set(entityReference.getEntityId());
			}

			@Override
			public void visit(ValueObjectValue valueObject) {
				Serializer<?> serializer = serializerRepository.getSerializer(valueObject.getActualMetaDataId());
				result.set(serializer.deserialize(valueObject.getValue()));
			}

			@Override
			public void visit(CollectionValue collectionValue) {
				deserialize(serializerRepository.getCollectionSerializer(collectionValue.getActualMetaDataId()),
						collectionValue);
			}

			private <C> void deserialize(CollectionSerializer<C> collectionSerializer,
					final CollectionValue collectionValue) {
				C collection = collectionSerializer.deserialize(collectionValue.getCollectionInfo());
				collectionSerializer.fill(collection, new Iterable<Object>() {
					@Override
					public Iterator<Object> iterator() {
						return new ValueDeserializer(collectionValue.getValues());
					}
				});
				result.set(collection);
			}
		});
		return result.get();
	}

	private class ValueDeserializer implements Iterator<Object> {

		private final Iterator<GenericValue> iterator;

		ValueDeserializer(ImmutableList<GenericValue> values) {
			this.iterator = values.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Object next() {
			return deserialize(iterator.next());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
