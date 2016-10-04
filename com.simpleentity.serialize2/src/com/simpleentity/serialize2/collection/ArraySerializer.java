package com.simpleentity.serialize2.collection;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.SerializationContext;
import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaData;

public class ArraySerializer implements CollectionSerializer<Object> {

	private final SerializationContext context;

	public ArraySerializer(SerializationContext context) {
		this.context = context;
	}

	@Override
	public CollectionInfo<Object> serialize(Object array) {
		Class<?> arrayType = array.getClass();
		if (!arrayType.isArray()) {
			throw new SerializerException("Requested to serialize non-array type: " + arrayType.getName());
		}
		EntityId elementMetaDataId = context.getSerializerRepository().getMetaDataId(arrayType.getComponentType());

		// TODO make constant
		ObjectInfo arrayInfo = ObjectInfo.newBuilder().setMetaDataId(BootStrap.ID_ARRAY).build();
		return new CollectionInfo<Object>(arrayInfo, elementMetaDataId, Array.getLength(array), new ArrayWrapper(array));
	}

	@Override
	public Object deserialize(CollectionInfo<Object> collectionInfo) {
		MetaData elementMetaData = context.getSerializerRepository().getMetaData(collectionInfo.getElementMetaDataId());
		Class<?> componentType = elementMetaData.getClass();

		Object array = Array.newInstance(componentType, collectionInfo.getElementCount());
		int i = 0;
		for (Object object : collectionInfo.getElements()) {
			Array.set(array, i, object);
			i++;
		}
		return array;
	}

	private static class ArrayWrapper implements Iterable<Object> {
		private final Object array;

		ArrayWrapper(Object array) {
			this.array = array;
		}

		@Override
		public Iterator<Object> iterator() {
			return new ArrayIterator(array);
		}
	}

	private static class ArrayIterator implements Iterator<Object> {

		private final Object array;
		private final int length;
		private int next = 0;

		ArrayIterator(Object array) {
			this.array = array;
			this.length = Array.getLength(array);
		}

		@Override
		public boolean hasNext() {
			return next < length;
		}

		@Override
		public Object next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Object result = Array.get(array, next);
			next++;
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
