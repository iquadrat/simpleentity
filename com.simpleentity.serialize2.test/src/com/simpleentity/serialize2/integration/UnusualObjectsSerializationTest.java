package com.simpleentity.serialize2.integration;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.povworld.collection.Map;
import org.povworld.collection.mutable.HashMap;
import org.povworld.collection.mutable.HashSet;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.annotation.ValueObject;
import com.simpleentity.entity.id.AtomicIdFactory;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.id.EntityIdFactory;
import com.simpleentity.serialize2.Serializer;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.integration.testclasses.ArrayRefs;
import com.simpleentity.serialize2.integration.testclasses.EnumClass;
import com.simpleentity.serialize2.integration.testclasses.GenericReference;
import com.simpleentity.serialize2.integration.testclasses.Key;
import com.simpleentity.serialize2.integration.testclasses.MultDimArray;
import com.simpleentity.serialize2.integration.testclasses.OuterClass;
import com.simpleentity.serialize2.integration.testclasses.OuterClass2;
import com.simpleentity.serialize2.integration.testclasses.TestEnum;
import com.simpleentity.serialize2.integration.testclasses.Value;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaDataFactory;
import com.simpleentity.serialize2.reflect.ObjenesisInstantiator;
import com.simpleentity.serialize2.reflect.ReflectiveMetaDataFactory;
import com.simpleentity.serialize2.repository.JavaSerializerRepository;
import com.simpleentity.util.bytes.ByteChunk;
import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

public class UnusualObjectsSerializationTest {

	private static final boolean PRINT_BYTES = true;

	private EntityIdFactory idFactory;
	private MetaDataFactory metaDataFactory;
	private JavaSerializerRepository serializerRepository;

	@Before
	public void setUp() {
		idFactory = new AtomicIdFactory(BootStrap.ID_RANGE_END + 1);
		metaDataFactory = new ReflectiveMetaDataFactory();
		serializerRepository = new JavaSerializerRepository(metaDataFactory, idFactory, getClass().getClassLoader(),
				new ObjenesisInstantiator());
	}

	private <T> T serializeAndDeserialize(T object) {
		ByteChunk bytes = serialize(object);
		if (PRINT_BYTES) {
			System.out.println(bytes);
		}
		return deserialize(bytes);
	}

	@SuppressWarnings("unchecked")
	private <T> ByteChunk serialize(T entity) {
		EntityId metaDataId = serializerRepository.getMetaDataId(entity.getClass());
		Serializer<T> serializer = (Serializer<T>) serializerRepository.getSerializer(metaDataId);
		ObjectInfo objectInfo = serializer.serialize(entity);
		ByteWriter destination = new ByteWriter();
		destination.putVarInt(metaDataId.getId());
		serializerRepository.getBinarySerializer(metaDataId).serialize(objectInfo, destination);
		return destination.build();
	}

	@SuppressWarnings("unchecked")
	private <T> T deserialize(ByteChunk bytes) {
		ByteReader source = new ByteReader(bytes);
		EntityId metaDataId = new EntityId(source.getVarInt());
		ObjectInfo objectInfo = serializerRepository.getBinarySerializer(metaDataId).deserialize(source);
		Serializer<T> serializer = (Serializer<T>) serializerRepository.getSerializer(metaDataId);
		return serializer.deserialize(objectInfo);
	}

	static class Primitives {
		public int a = 7;
		protected short b = -9;
		boolean c = false;
		byte d = 127;
		long e = 7837128223L;
		char f = 'x';
		final float g = -12383.123f;
		public final double h = 9992992.12312;
	}

	@Test
	public void serializePrimitives() {
		Primitives p = new Primitives();
		Primitives actual = serializeAndDeserialize(p);

		assertEquals(p.a, actual.a);
		assertEquals(p.b, actual.b);
		assertEquals(p.c, actual.c);
		assertEquals(p.d, actual.d);
		assertEquals(p.e, actual.e);
		assertEquals(p.f, actual.f);
		assertEquals(p.g, actual.g, 0);
		assertEquals(p.h, actual.h, 0);
	}

	// TODO
	public void serializePseudoPrimitveTypes() {
		Integer i = Integer.valueOf(-471223);
		assertEquals(i, serializeAndDeserialize(i));

		Short s = Short.valueOf((short) 223);
		assertEquals(s, serializeAndDeserialize(s));

		Byte b = Byte.valueOf((byte) -123);
		assertEquals(b, serializeAndDeserialize(b));

		Character c = Character.valueOf('5');
		assertEquals(c, serializeAndDeserialize(c));

		Boolean bool = Boolean.TRUE;
		assertEquals(bool, serializeAndDeserialize(bool));

		Float f = Float.valueOf(41234234.12f);
		assertEquals(f, serializeAndDeserialize(f));

		Double d = Double.valueOf(-1283723123.234234);
		assertEquals(d, serializeAndDeserialize(d));
	}

	static class Transient {
		int i = 761;
		transient int j = 312;
		transient boolean b1 = true;
		boolean b2 = true;
	}

	@Test
	public void transientFieldIsNotIgnored() {
		Transient t = new Transient();
		Transient actual = serializeAndDeserialize(t);
		assertEquals(t.i, actual.i);
		assertEquals(t.j, actual.j);
		assertEquals(t.b1, actual.b1);
		assertEquals(t.b2, actual.b2);
	}

	@ValueObject
	static class Reference {
		final @CheckForNull Object ref;
		final @CheckForNull Reference add;
		Reference(@CheckForNull Object ref, @CheckForNull Reference add) {
			this.ref = ref;
			this.add = add;
		}
	}

	@Test
	public void nullReference() {
		Reference r = new Reference(null, null);
		Reference actual = serializeAndDeserialize(r);
		assertSame(null, actual.ref);
		assertSame(null, actual.add);
	}

	// TODO
	public void test_enum_as_field_reference() {
		EnumClass r = new EnumClass();
		r.value = TestEnum.TWO;
		EnumClass actual = serializeAndDeserialize(r);
		assertEquals(r.value, actual.value);

		r.value = null;
		actual = serializeAndDeserialize(r);
		assertNull(actual.value);
	}

	// TODO
	public void test_enum_as_object_reference() {
		Reference r = new Reference(TestEnum.THREE, null);
		Reference actual = serializeAndDeserialize(r);
		assertEquals(r.ref, actual.ref);
	}

	// TODO
	public void test_enum_instances() {
		assertSame(TestEnum.ONE, serializeAndDeserialize(TestEnum.ONE));
		assertSame(TestEnum.TWO, serializeAndDeserialize(TestEnum.TWO));
		assertSame(TestEnum.THREE, serializeAndDeserialize(TestEnum.THREE));
	}

	@ValueObject
	static class TypedReference {
		Integer integer;
		String string;
		ByteBuffer buffer;
	}

	@Test
	public void typedReference() {
		TypedReference ref = new TypedReference();
		ref.integer = Integer.valueOf(-1);
		ref.string = "Hello World!";
		ref.buffer = ByteBuffer.wrap("kitty catty".getBytes());

		TypedReference actual = serializeAndDeserialize(ref);

		assertEquals(ref.integer, actual.integer);
		assertEquals(ref.string, actual.string);
		assertEquals(ref.buffer, actual.buffer);
	}

	@Test
	public void genericReference() {
		GenericReference<Short> ref = new GenericReference<Short>(null, null);
		GenericReference<Short> actual = serializeAndDeserialize(ref);
		assertNull(actual.ref);
		assertNull(actual.list);

		ref = new GenericReference<Short>(Short.valueOf((short) 7162), new Vector<Short>());
		ref.list.add(Short.valueOf((short) 17));
		ref.list.add(Short.valueOf((short) 0));
		ref.list.add(Short.valueOf((short) -1217));
		ref.list.add(Short.valueOf((short) 999));

		actual = serializeAndDeserialize(ref);
		assertEquals(ref.ref, actual.ref);
		assertEquals(ref.list, actual.list);
		assertEquals(ref.list.getClass(), actual.list.getClass());
	}

	@ValueObject
	static class PrimitiveArrays {
		public int[] ints = { 1, 2, 3, 4, 5, 6 };
		short[] shorts = { -1, -2, -7 };
		final byte[] bytes = { -128, 127, 0, 1 };
		volatile protected char[] chars = { 'h', 'e', 'l', 'l', 'o' };
		long[] longs = { 18237812738L, -123123L, 99183, 123 };
		float[] floats = { 0.f, -12323.f, 891283f };
		double[] doubles = { 129382E22, 782E-12, 1f };
		int[] zerolength = {};
		@CheckForNull short[] nullref = null;
	}

	@Test
	public void primitiveArrays() {
		PrimitiveArrays arrays = new PrimitiveArrays();
		PrimitiveArrays actual = serializeAndDeserialize(arrays);

		assertTrue(Arrays.equals(arrays.ints, actual.ints));
		assertTrue(Arrays.equals(arrays.shorts, actual.shorts));
		assertTrue(Arrays.equals(arrays.bytes, actual.bytes));
		assertTrue(Arrays.equals(arrays.chars, actual.chars));
		assertTrue(Arrays.equals(arrays.longs, actual.longs));
		assertTrue(Arrays.equals(arrays.floats, actual.floats));
		assertTrue(Arrays.equals(arrays.doubles, actual.doubles));
		assertTrue(Arrays.equals(arrays.zerolength, actual.zerolength));
		assertSame(null, actual.nullref);
	}

	@Test
	public void arrayAsObjectReference() {
		// TODO investigate serialized bytes: Could be more compact.
		int[] ints = new int[] { 78, 12, -3 };
		Reference r = new Reference(ints, null);
		Reference actual = serializeAndDeserialize(r);
		assertTrue(Arrays.equals(ints, (int[]) actual.ref));
	}

	// TODO
	public void referenceArray() {
		Object[] refs = new Object[4];
		refs[0] = Integer.valueOf(27);
		refs[1] = new int[] { 1, 2 };
		refs[2] = TestEnum.ONE;

		Object[] actual = serializeAndDeserialize(refs);

		assertEquals(refs[0], actual[0]);
		assertTrue(Arrays.equals((int[]) refs[1], (int[]) actual[1]));
		assertSame(refs[2], actual[2]);
		assertSame(refs[3], actual[3]);
	}

	@Test
	public void arrayDirectAndReferences() {
		ArrayRefs array = new ArrayRefs();
		array.values = new int[] { 56, 1, -2 };
		array.values2 = array.values;
		array.chars = new char[] { 'a', 'b', 'a', 'd' };
		array.ref = null;

		ArrayRefs actual = serializeAndDeserialize(array);
		assertArrayEquals(actual.values, actual.values2);

		array.ref = array.values;
		actual = serializeAndDeserialize(array);
		assertArrayEquals(actual.values, actual.values2);
		assertArrayEquals(actual.values, (int[])actual.ref);
		assertArrayEquals(actual.chars, array.chars);
	}

	// TODO
	public void test_enum_array_and_set() {
		TestEnum[] enums = new TestEnum[] { TestEnum.ONE, TestEnum.THREE, TestEnum.THREE, null, TestEnum.ONE };
		TestEnum[] actual = serializeAndDeserialize(enums);
		assertTrue(Arrays.equals(enums, actual));

		EnumSet<TestEnum> enumSet = EnumSet.of(TestEnum.THREE, TestEnum.TWO);
		EnumSet<TestEnum> actual2 = serializeAndDeserialize(enumSet);
		assertEquals(enumSet, actual2);
	}

	@Test
	public void multidimensionalArraysNull() {
		MultDimArray arrays = new MultDimArray();
		MultDimArray actual = serializeAndDeserialize(arrays);
		assertNull(actual.a2);
		assertNull(actual.a3);
		assertNull(actual.a4);
	}

	@Test
	public void multidimensionalArraysEmpty() {
		MultDimArray arrays = new MultDimArray();
		arrays.a2 = new short[][] { {} };
		arrays.a3 = new String[][][] { { {} } };
		arrays.a4 = new int[][][][] { { { {} } } };

		MultDimArray actual = serializeAndDeserialize(arrays);
		assertTrue(Arrays.deepEquals(arrays.a2, actual.a2));
		assertTrue(Arrays.deepEquals(arrays.a3, actual.a3));
		assertTrue(Arrays.deepEquals(arrays.a4, actual.a4));
	}

	@Test
	public void multidimensionalArraysFilled() {
		MultDimArray arrays = new MultDimArray();
		arrays.a2 = new short[3][];
		arrays.a3 = new String[2][][];
		arrays.a4 = new int[2][3][4][2];

		arrays.a2[0] = new short[] { -5, 12, 73 };
		arrays.a2[1] = new short[] { 0 };
		arrays.a2[2] = new short[] { 1, 2, 3, 4, 5, 6, 7 };

		arrays.a3[0] = new String[][] { { "foo", "hello" }, null, { null, "x" } };
		arrays.a3[0] = new String[][] { { "tre" } };
		arrays.a3[1] = new String[][] { null, { "" } };

		arrays.a4[0][0][1][1] = 1324;
		arrays.a4[1][2][2][0] = -123723;
		arrays.a4[1][0][0][1] = 11111;
		arrays.a4[1][1][1][1] = -1;

		MultDimArray actual = serializeAndDeserialize(arrays);
		assertTrue(Arrays.deepEquals(arrays.a2, actual.a2));
		assertTrue(Arrays.deepEquals(arrays.a3, actual.a3));
		assertTrue(Arrays.deepEquals(arrays.a4, actual.a4));
	}

	@Test
	public void serializeInnerClass() {
		OuterClass outer = new OuterClass();
		OuterClass.InnerClass inner = outer.new InnerClass();
		OuterClass.InnerClass actual = serializeAndDeserialize(inner);
		assertEquals(inner.name, actual.name);
		assertEquals(inner.value, actual.value);
		assertEquals(inner.getText(), actual.getText());
		assertEquals(inner.getClass(), actual.getClass());
	}

	@Test
	public void serializeInnerClass2() {
		OuterClass2 outer = new OuterClass2();
		Object inner = outer.createInner();
		Object actual = serializeAndDeserialize(inner);
		assertNotNull(actual);
	}

	private static com.simpleentity.entity.value.ValueObject createAnonymousValue() {
		return new com.simpleentity.entity.value.ValueObject() {
			@SuppressWarnings("unused")
			String value = "foo";
		};
	}

	@Test
	public void serializeAnonymousClass() {
		com.simpleentity.entity.value.ValueObject anonymous = createAnonymousValue();
		com.simpleentity.entity.value.ValueObject actual = serializeAndDeserialize(anonymous);
		assertEquals(anonymous, actual);
	}

	static class MapValue extends com.simpleentity.entity.value.ValueObject {
		HashMap<Key, Value> map = new HashMap<Key, Value>();
	}

	@Test
	public void mapValues() {
		MapValue mapValue = new MapValue();
		Value v1 = new Value("A", 1);
		Value v2 = new Value("B", 2);

		mapValue.map.put(new Key(19), v1);
		mapValue.map.put(new Key(93), v2);
		mapValue.map.put(new Key(11), v1);

		MapValue actual = serializeAndDeserialize(mapValue);
		assertEquals(mapValue, actual);
	}

	// TODO write custom serializer for Date
	public void serializeDate() {
		Date date = new Date();
		Date actual = serializeAndDeserialize(date);
		assertEquals(date, actual);
	}

	static class SetKey extends com.simpleentity.entity.value.ValueObject {
		private final HashSet<Key> keys = new HashSet<Key>();

		public SetKey(Key... keySet) {
			keys.addAll(Arrays.asList(keySet));
		}
	}

	@Test
	public void mapWithSetKey() {
		HashMap<SetKey, Value> map = new HashMap<SetKey, Value>();
		Value v1 = new Value("foo", 44.4);
		map.put(new SetKey(new Key(12), new Key(841), new Key(11)), v1);
		Value v2 = new Value("empty", 0.01);
		map.put(new SetKey(), v2);
		Value v3 = new Value("two", 22);
		map.put(new SetKey(new Key(1), new Key(2)), v3);

		System.out.println(map);

		Map<SetKey, Value> actual = serializeAndDeserialize(map);
		assertEquals(3, actual.size());
		assertEquals(v1, map.get(new SetKey(new Key(12), new Key(841), new Key(11))));
		assertEquals(v2, map.get(new SetKey()));
		assertEquals(v3, map.get(new SetKey(new Key(1), new Key(2))));
	}

}
