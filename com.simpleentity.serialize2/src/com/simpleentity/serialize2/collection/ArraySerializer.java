package com.simpleentity.serialize2.collection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.povworld.collection.common.MathUtil;
import org.povworld.collection.common.PreConditions;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.GenericValue.EntityIdValue;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaDataRepository;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.serialize2.meta.Type;
import com.simpleentity.util.TypeUtil;

public class ArraySerializer implements CollectionSerializer<Object> {

	private static final String FIELD_DIMENSIONS = "dimensions";
	private static final String FIELD_LEAF_META_DATA = "leafMetaData";
	private static final String FIELD_PRIMITIVE = "primitive";

	private final MetaDataRepository repository;
	private final ClassLoader classLoader;

	public ArraySerializer(MetaDataRepository respository, ClassLoader classLoader) {
		this.repository = respository;
		this.classLoader = classLoader;
	}

	@Override
	public CollectionInfo serialize(Object array) {
		Class<?> arrayType = array.getClass();
		if (!arrayType.isArray()) {
			throw new SerializerException("Requested to serialize non-array type: " + arrayType.getName());
		}

		Class<?> componentType = TypeUtil.getArrayElementType(arrayType);
		int dimension = TypeUtil.getArrayDimension(arrayType);
		boolean primitive = componentType.isPrimitive();

		EntityId leafMetaDataId = repository.getMetaDataId(componentType);
		ObjectInfo arrayInfo;
		EntityId elementMetaDataId = leafMetaDataId;
		if (dimension == 1) {
			// TODO make constants
			arrayInfo = ObjectInfo.newBuilder().setMetaDataId(primitive ? BootStrap.ID_PRIMITIVE_ARRAY : BootStrap.ID_OBJECT_ARRAY).build();
		} else {
			elementMetaDataId = repository.getMetaDataId(arrayType.getComponentType());
			arrayInfo = ObjectInfo.newBuilder()
					.setMetaDataId(BootStrap.ID_MULTI_DIMENSIONAL_ARRAY)
					.setEntryValue(FIELD_DIMENSIONS, GenericValue.varIntValue(dimension))
					.setEntryValue(FIELD_PRIMITIVE, GenericValue.booleanValue(primitive))
					.setEntryValue(FIELD_LEAF_META_DATA, new EntityIdValue(leafMetaDataId))
					.build();
		}

		return new CollectionInfo(arrayInfo, elementMetaDataId, Array.getLength(array), new ArrayWrapper(array));
	}


	@Override
	public Object deserialize(CollectionInfo collectionInfo) {
		Object array = createArrayInstance(collectionInfo);
		int i = 0;
		for (Object object : collectionInfo.getElements()) {
			Array.set(array, i, object);
			i++;
		}
		return array;
	}

	private Object createArrayInstance(CollectionInfo collectionInfo) {
		EntityId arrayMetaDataId = collectionInfo.getCollectionInfo().getMetaTypeId();
		EntityId componentMetaDataId;
		int dimensions;
		boolean primitive;
		if (BootStrap.ID_MULTI_DIMENSIONAL_ARRAY.equals(arrayMetaDataId)) {
			componentMetaDataId = collectionInfo.getCollectionInfo().getEntityIdValue(FIELD_LEAF_META_DATA).getEntityId();
			dimensions = MathUtil.longToInt((long)collectionInfo.getCollectionInfo().getPrimitiveValue(FIELD_DIMENSIONS).getValue());
			primitive = (boolean)collectionInfo.getCollectionInfo().getPrimitiveValue(FIELD_PRIMITIVE).getValue();
		} else {
			componentMetaDataId = collectionInfo.getElementMetaDataId();
			dimensions = 1;
			primitive = (BootStrap.ID_PRIMITIVE_ARRAY.equals(arrayMetaDataId));
		}
		Class<?> componentType = getComponentType(componentMetaDataId, dimensions, primitive);
		return Array.newInstance(componentType, collectionInfo.getElementCount());
	}

	private Class<?> getComponentType(EntityId metaDataId, int dimensions, boolean primitive)  {
		Class<?> componentType;
		if (primitive) {
			componentType = Primitive.byEntityId(metaDataId).getType();
		} else {
			try {
				MetaData elementMetaData = repository.getMetaData(metaDataId);
				componentType = classLoader.loadClass(elementMetaData.getClassName());
			} catch (ClassNotFoundException e) {
				throw new SerializerException(e);
			}
		}
		if (dimensions > 1) {
			// TODO this produces garbage instance
			componentType = Array.newInstance(componentType, new int[dimensions-1]).getClass();
		}
		return componentType;
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

	@Override
	public Type getElementType(Field field) {
		PreConditions.paramCheck(field, "Is not an array type!", field.getType().isArray());
		return getType(field.getType().getComponentType());
	}

	private Type getType(Class<?> type) {
		if (type.isArray()) {
			Type elementType = getType(type.getComponentType());
			if (elementType.isCollectionType()) {
				return new Type(BootStrap.ID_MULTI_DIMENSIONAL_ARRAY, true, elementType);
			}
			boolean primitive = type.getComponentType().isPrimitive();
			return new Type(primitive ? BootStrap.ID_PRIMITIVE_ARRAY : BootStrap.ID_OBJECT_ARRAY, true, elementType);
		}
		boolean optional = !type.isPrimitive();
		return new Type(repository.getMetaDataId(type), optional);
	}

}
