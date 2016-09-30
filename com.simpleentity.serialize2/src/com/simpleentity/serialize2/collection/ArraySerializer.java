package com.simpleentity.serialize2.collection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.povworld.collection.Collection;
import org.povworld.collection.common.MathUtil;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.serialize2.SerializationContext;
import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.GenericValue.PrimitiveValue;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.serialize2.meta.Type;
import com.simpleentity.util.TypeUtil;

public class ArraySerializer implements CollectionSerializer<Object> {

	private final SerializationContext context;

	public ArraySerializer(SerializationContext context) {
		this.context = context;
	}

	@Override
	public ObjectInfo serialize(Object object) {
		Class<?> arrayType = object.getClass();
		if (!arrayType.isArray()) {
			throw new SerializerException("Requested to serialize non-array type: " + arrayType.getName());
		}
		int dimension = TypeUtil.getArrayDimension(arrayType);
		Class<?> elementType = TypeUtil.getArrayElementType(arrayType);

		return ObjectInfo.newBuilder().setMetaDataId(BootStrap.ID_ARRAY)
				.setEntryValue("dimension", GenericValue.varIntValue(dimension))
				.setEntryValue("componentType", GenericValue.stringValue(elementType.getName()))
				.setEntryValue("length", GenericValue.varIntValue(Array.getLength(object))).build();
	}

	@Override
	public Object deserialize(ObjectInfo objectInfo) {
		PrimitiveValue value = objectInfo.getPrimitiveValue("componentType");
		checkPrimitiveValue("componentType", value, Primitive.STRING);
		Class<?> componentType;
		try {
			componentType = context.getClassLoader().loadClass((String) value.getValue());
		} catch (ClassNotFoundException e) {
			throw new SerializerException(e);
		}

		PrimitiveValue dimension = objectInfo.getPrimitiveValue("dimension");
		checkPrimitiveValue("dimension", value, Primitive.VARINT);

		PrimitiveValue length = objectInfo.getPrimitiveValue("length");
		checkPrimitiveValue("length", value, Primitive.VARINT);
		int[] lengths = new int[MathUtil.longToInt((long) dimension.getValue())];
		lengths[0] = MathUtil.longToInt((long) length.getValue());
		return Array.newInstance(componentType, lengths);
	}

	private void checkPrimitiveValue(String entry, @CheckForNull PrimitiveValue value, Primitive type) {
		if (value == null) {
			throw new SerializerException("Missing required entry '" + entry + "'!");
		}
		if (value.getType() != type) {
			throw new SerializerException("PrimitiveValue for '" + entry + "' has wrong type: Expected " + type
					+ " but was " + value.getType());
		}
	}

	@Override
	public Class<Object> getType() {
		return Object.class;
	}

	@Override
	public void fill(Object array, Iterable<?> elements) {
		checkIsArrayInstance(array);
		int i = 0;
		for (Object element : elements) {
			Array.set(array, i++, element);
		}
	}

	@Override
	public Collection<?> asCollection(Object array) {
		checkIsArrayInstance(array);
		return new ArrayWrapper(array);
	}

	private static class ArrayWrapper implements Collection<Object> {

		private final Object array;

		ArrayWrapper(Object array) {
			this.array = array;
		}

		@Override
		public Iterator<Object> iterator() {
			return new ArrayIterator(array);
		}

		@Override
		public int size() {
			return Array.getLength(array);
		}

		@Override
		@CheckForNull
		public Object getFirstOrNull() {
			return isEmpty() ? null : Array.get(array, 0);
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public Object getFirst() throws NoSuchElementException {
			if (isEmpty())
				throw new NoSuchElementException();
			return Array.get(array, 0);
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

	@Override
	public Type getElementType(Field field, SerializerRepository serializerRepository) {
		// TODO check that it is an array
		return new Type(serializerRepository.getMetaDataId(field.getType().getComponentType()), false);
	}

	private void checkIsArrayInstance(Object object) {
		if (!object.getClass().isArray()) {
			throw new SerializerException("Requested to serialize non-array object: " + object);
		}
	}

}
