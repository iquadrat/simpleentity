package com.simpleentity.serialize2.collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.povworld.collection.immutable.ImmutableCollections;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.MockIdFactory;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.GenericValue.EntityIdValue;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaData.Builder;
import com.simpleentity.serialize2.meta.MetaDataUtil;
import com.simpleentity.serialize2.meta.MetaType;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.serialize2.meta.Type;

/** Unit tests for {@link ArraySerializer}. */
@RunWith(MockitoJUnitRunner.class)
public class ArraySerializerTest {

	private static final String TEST_DOMAIN = "test";
	private static final long TEST_VERSION = 42;

	private final MockIdFactory idFactory = new MockIdFactory();

 	@Mock
 	private SerializerRepository repository;
	private ArraySerializer serializer;

	@Before
	public void setUp() {
		for(Primitive primitive: Primitive.values()) {
			if (primitive == Primitive.VARINT) continue;
			when(repository.getMetaDataId(primitive.getType())).thenReturn(primitive.getMetaDataId());
			when(repository.getMetaDataId(primitive.getBoxedType())).thenReturn(primitive.getMetaDataId());
			when(repository.getMetaData(primitive.getType())).thenReturn(primitive.getMetaData());
			when(repository.getMetaData(primitive.getBoxedType())).thenReturn(primitive.getMetaData());
			when(repository.getMetaData(primitive.getMetaDataId())).thenReturn(primitive.getMetaData());
		}
		serializer = new ArraySerializer(repository, getClass().getClassLoader());
	}

	@Test
	public void serialize0LengthArray() {
		int[] array = new int[0];
		CollectionInfo actual = serializer.serialize(array);
		CollectionInfo expected = new CollectionInfo(
				ObjectInfo.newBuilder()
					.setMetaDataId(BootStrap.ID_PRIMITIVE_ARRAY)
					.build(),
				BootStrap.ID_PRIMITIVE_INT,
				ImmutableCollections.asList());
		assertEquals(expected, actual);
		int[] array1 = (int[])serializer.deserialize(actual);
		assertArrayEquals(array, array1);
	}

	@Test
	public void serializePrimitiveArray() {
		byte[] array = new byte[] {1,2,3};
		CollectionInfo actual = serializer.serialize(array);
		CollectionInfo expected = new CollectionInfo(
				ObjectInfo.newBuilder()
					.setMetaDataId(BootStrap.ID_PRIMITIVE_ARRAY)
					.build(),
				BootStrap.ID_PRIMITIVE_BYTE,
				ImmutableCollections.asList((byte)1,(byte)2,(byte)3));
		assertEquals(expected, actual);
		byte[] array1 = (byte[])serializer.deserialize(actual);
		assertArrayEquals(array, array1);
	}

	@Test
	public void serializeBoxedPrimitiveArray() {
		Integer[] array = new Integer[] {1,2,3};
		CollectionInfo actual = serializer.serialize(array);
		CollectionInfo expected = new CollectionInfo(
				ObjectInfo.newBuilder()
					.setMetaDataId(BootStrap.ID_OBJECT_ARRAY)
					.build(),
				BootStrap.ID_PRIMITIVE_INT,
				ImmutableCollections.asList(1,2,3));
		assertEquals(expected, actual);
		Integer[] array1 = (Integer[])serializer.deserialize(actual);
		assertArrayEquals(array, array1);
	}

	static class Value {}

	@Test
	public void serializeValueObjectArray() {
		MetaData valueMetaData = prepareMetaData(Value.class, MetaType.VALUE_OBJECT);

		Value[] values = new Value[] {new Value(), new Value()};

		CollectionInfo actual = serializer.serialize(values);
		CollectionInfo expected = new CollectionInfo(
				ObjectInfo.newBuilder()
					.setMetaDataId(BootStrap.ID_OBJECT_ARRAY)
					.build(),
				valueMetaData.getEntityId(), 2, Arrays.asList(values));
		assertEquals(expected, actual);
		Value[] array1 = (Value[])serializer.deserialize(actual);
		assertArrayEquals(values, array1);
	}

	@Test
	public void serializeObjectArray() {
		when(repository.getMetaDataId(Object.class)).thenReturn(BootStrap.ID_ANY);
		when(repository.getMetaData(BootStrap.ID_ANY)).thenReturn(BootStrap.ANY);

		Object[] values = new Object[] {new Value(), new Value()};

		CollectionInfo actual = serializer.serialize(values);
		CollectionInfo expected = new CollectionInfo(
				ObjectInfo.newBuilder()
					.setMetaDataId(BootStrap.ID_OBJECT_ARRAY)
					.build(),
				BootStrap.ID_ANY, 2, Arrays.asList(values));
		assertEquals(expected, actual);
		Object[] array1 = (Object[])serializer.deserialize(actual);
		assertArrayEquals(values, array1);
	}

	@Test
	public void serialize2dPrimitiveArray() {
		MetaData intArray = prepareMetaData(int[].class, MetaType.COLLECTION);
		int[][] array = new int[][] {{},{1},{1,2}};
		CollectionInfo actual = serializer.serialize(array);
		CollectionInfo expected = new CollectionInfo(
				ObjectInfo.newBuilder()
					.setMetaDataId(BootStrap.ID_MULTI_DIMENSIONAL_ARRAY)
					.setEntryValue("dimensions", GenericValue.varIntValue(2))
					.setEntryValue("primitive", GenericValue.booleanValue(true))
					.setEntryValue("leafMetaData", new EntityIdValue(BootStrap.ID_PRIMITIVE_INT))
					.build(),
				intArray.getEntityId(), 3, Arrays.asList(array[0], array[1], array[2]));
		assertEquals(expected, actual);
		int[][] array1 = (int[][])serializer.deserialize(actual);
		assertArrayEquals(array, array1);
	}

	private MetaData prepareMetaData(Class<?> class_, MetaType metaType) {
		Mockito.when(repository.getMetaDataId(class_)).thenReturn(new EntityId(idFactory.nextId.get()));
		String domain = TEST_DOMAIN;
		if (!class_.getName().startsWith(domain)) {
			int top = class_.getName().indexOf('.');
			domain = (top == -1) ? "" : class_.getName().substring(0,top);
		}

		Builder builder = MetaData.newBuilder()
			.setClassName(class_.getName())
			.setDomain(domain)
			.setVersion(TEST_VERSION)
			.setMetaType(metaType)
			.build(idFactory)
			.toBuilder();
		MetaDataUtil.addFieldsToMetaDataEntries(class_, repository, builder);
		MetaData result = builder.build(idFactory);
		Mockito.when(repository.getMetaData(class_)).thenReturn(result);
		Mockito.when(repository.getMetaData(result.getEntityId())).thenReturn(result);
		return result;
	}

	int[] ints;
	Integer[] integers;
	Value[] values;

	int[][] ints2d;
	Value[][][] values3d;

	private Field getField(String name) throws Exception {
		return getClass().getDeclaredField(name);
	}

	@Test
	public void getElementType() throws Exception {
		MetaData value = prepareMetaData(Value.class, MetaType.VALUE_OBJECT);

		Type intType = new Type(BootStrap.ID_PRIMITIVE_INT, false);
		Type intOptType = new Type(BootStrap.ID_PRIMITIVE_INT, true);
		Type valueOptType = new Type(value.getEntityId(), true);

		assertEquals(intType, serializer.getElementType(getField("ints")));
		assertEquals(intOptType, serializer.getElementType(getField("integers")));
		assertEquals(valueOptType, serializer.getElementType(getField("values")));

		assertEquals(new Type(BootStrap.ID_PRIMITIVE_ARRAY, true, intType),
				serializer.getElementType(getField("ints2d")));

		assertEquals(new Type(BootStrap.ID_MULTI_DIMENSIONAL_ARRAY, true,
								new Type(BootStrap.ID_OBJECT_ARRAY, true, valueOptType)),
				serializer.getElementType(getField("values3d")));
	}

}
