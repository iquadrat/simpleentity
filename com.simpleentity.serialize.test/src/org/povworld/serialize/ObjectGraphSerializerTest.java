/*
 * Created on Nov 30, 2007
 *
 */
package org.povworld.serialize;

import java.nio.ByteBuffer;
import java.util.*;

import org.povworld.serialize.testclasses.*;

public class ObjectGraphSerializerTest extends AbstractSerializationTest {

  static class Primitives {
    public int          a = 7;
    protected short     b = -9;
    boolean             c = false;
    byte                d = 127;
    long                e = 7837128223L;
    char                f = 'x';
    final float         g = -12383.123f;
    public final double h = 9992992.12312;
  }

  public void test_serialize_primitve_types() {
    Primitives p = new Primitives();
    Primitives actual = serializeAndDeserialize(p);

    assertEquals(p.a, actual.a);
    assertEquals(p.b, actual.b);
    assertEquals(p.c, actual.c);
    assertEquals(p.d, actual.d);
    assertEquals(p.e, actual.e);
    assertEquals(p.f, actual.f);
    assertEquals(p.g, actual.g);
    assertEquals(p.h, actual.h);
  }

  public void test_serialize_pseudo_primitve_types() {
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
    int               i  = 761;
    transient int     j  = 312;
    transient boolean b1 = true;
    boolean           b2 = true;
  }

  public void test_transient_field_is_ignored() {
    Transient t = new Transient();
    Transient actual = serializeAndDeserialize(t);
    assertEquals(t.i, actual.i);
    assertEquals(0, actual.j);
    assertEquals(false, actual.b1);
    assertEquals(t.b2, actual.b2);
  }

  static class Reference {
    Object ref = null;
    Object add = null;
  }

  public void test_null_reference() {
    Reference r = new Reference();
    Reference actual = serializeAndDeserialize(r);
    assertSame(null, actual.ref);
    assertSame(null, actual.add);
  }

  public void test_self_reference() {
    Reference r = new Reference();
    r.ref = r;
    Reference actual = serializeAndDeserialize(r);
    assertSame(actual, actual.ref);
    assertSame(null, actual.add);
  }

  public void test_enum_as_field_reference() {
    EnumClass r = new EnumClass();
    r.value = TestEnum.TWO;
    EnumClass actual = serializeAndDeserialize(r);
    assertEquals(r.value, actual.value);

    r.value = null;
    actual = serializeAndDeserialize(r);
    assertNull(actual.value);
  }

  public void test_enum_as_object_reference() {
    Reference r = new Reference();
    r.ref = TestEnum.THREE;
    Reference actual = serializeAndDeserialize(r);
    assertEquals(r.ref, actual.ref);
  }

  public void test_enum_instances() {
    assertSame(TestEnum.ONE, serializeAndDeserialize(TestEnum.ONE));
    assertSame(TestEnum.TWO, serializeAndDeserialize(TestEnum.TWO));
    assertSame(TestEnum.THREE, serializeAndDeserialize(TestEnum.THREE));
  }

  public void test_reference_mesh() {
    Reference r0 = new Reference();
    Reference r1 = new Reference();
    Reference r2 = new Reference();
    Reference r3 = new Reference();
    Reference r4 = new Reference();
    Reference r5 = new Reference();
    Reference r6 = new Reference();
    Reference r7 = new Reference();

    r0.ref = r1;
    r0.add = r2;

    r1.ref = r3;
    r1.add = r4;

    r2.ref = r4;
    r2.add = r5;

    r3.ref = r3;
    r3.add = r4;

    r4.ref = r0;
    r4.add = r6;

    r5.ref = r7;
    r5.add = r2;

    r6.ref = r7;
    r6.add = r7;

    r7.ref = r1;
    r7.add = null;

    Reference a0 = serializeAndDeserialize(r0);

    Reference a1 = (Reference) a0.ref;
    Reference a2 = (Reference) a0.add;
    Reference a3 = (Reference) a1.ref;
    Reference a4 = (Reference) a1.add;
    Reference a5 = (Reference) a2.add;
    Reference a6 = (Reference) a4.add;
    Reference a7 = (Reference) a5.ref;

    assertSame(a1, a0.ref);
    assertSame(a2, a0.add);
    assertSame(a3, a1.ref);
    assertSame(a4, a1.add);
    assertSame(a4, a2.ref);
    assertSame(a5, a2.add);
    assertSame(a3, a3.ref);
    assertSame(a4, a3.add);
    assertSame(a0, a4.ref);
    assertSame(a6, a4.add);
    assertSame(a7, a5.ref);
    assertSame(a2, a5.add);
    assertSame(a7, a6.ref);
    assertSame(a7, a6.add);
    assertSame(a1, a7.ref);
    assertSame(null, a7.add);
  }

  static class TypedReference {
    Integer    integer;
    String     string;
    ByteBuffer buffer;
  }

  public void test_typed_reference() {
    TypedReference ref = new TypedReference();
    ref.integer = Integer.valueOf(-1);
    ref.string = "Hello World!";
    ref.buffer = ByteBuffer.wrap("kitty catty".getBytes());

    TypedReference actual = serializeAndDeserialize(ref);

    assertEquals(ref.integer, actual.integer);
    assertEquals(ref.string, actual.string);
    assertEquals(ref.buffer, actual.buffer);
  }

  public void test_generic_reference() {
    GenericReference<Short> ref = new GenericReference<Short>();

    GenericReference<Short> actual = serializeAndDeserialize(ref);
    assertNull(actual.ref);
    assertNull(actual.list);

    ref.ref = Short.valueOf((short) 7162);
    ref.list = new Vector<Short>();
    ref.list.add(Short.valueOf((short) 17));
    ref.list.add(Short.valueOf((short) 0));
    ref.list.add(Short.valueOf((short) -1217));
    ref.list.add(Short.valueOf((short) 999));

    actual = serializeAndDeserialize(ref);
    assertEquals(ref.ref, actual.ref);
    assertEquals(ref.list, actual.list);
    assertEquals(ref.list.getClass(), actual.list.getClass());
  }

  static class PrimitiveArrays {
    public int[]              ints       = { 1, 2, 3, 4, 5, 6 };
    short[]                   shorts     = { -1, -2, -7 };
    final byte[]              bytes      = { -128, 127, 0, 1 };
    volatile protected char[] chars      = { 'h', 'e', 'l', 'l', 'o' };
    long[]                    longs      = { 18237812738L, -123123L, 99183, 123 };
    float[]                   floats     = { 0.f, -12323.f, 891283f };
    double[]                  doubles    = { 129382E22, 782E-12, 1f };
    int[]                     zerolength = {};
    short[]                   nullref    = null;
  }

  public void test_primitive_arrays() {
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

  public void test_array_as_object_reference() {
    Reference r = new Reference();
    int[] ints = new int[] { 78, 12, -3 };
    r.ref = ints;

    Reference actual = serializeAndDeserialize(r);
    assertTrue(Arrays.equals(ints, (int[]) actual.ref));
  }

  public void test_array_instances() {
    byte[] bytes = new byte[] { 1, 2, 3, 4, 5 };
    byte[] actual = serializeAndDeserialize(bytes);
    assertTrue(Arrays.equals(bytes, actual));
  }

  public void test_reference_array() {
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

  public void test_array_direct_and_references() {
    ArrayRefs array = new ArrayRefs();
    array.values = new int[] { 56, 1, -2 };
    array.values2 = array.values;
    array.bools = new boolean[] {true, true, false, true, false};
    array.ref = null;

    ArrayRefs actual = serializeAndDeserialize(array);
    assertTrue(actual.values == actual.values2);

    array.ref = array.values;
    actual = serializeAndDeserialize(array);
    assertTrue(actual.values == actual.values2);
    assertTrue(actual.values == actual.ref);
    assertTrue(Arrays.equals(actual.bools, array.bools));
  }

  public void test_enum_array_and_set() {
    TestEnum[] enums = new TestEnum[] { TestEnum.ONE, TestEnum.THREE, TestEnum.THREE, null, TestEnum.ONE };
    TestEnum[] actual = serializeAndDeserialize(enums);
    assertTrue(Arrays.equals(enums, actual));

    EnumSet<TestEnum> enumSet = EnumSet.of(TestEnum.THREE, TestEnum.TWO);
    EnumSet<TestEnum> actual2 = serializeAndDeserialize(enumSet);
    assertEquals(enumSet, actual2);
  }

  public void test_multidimensional_arrays_null() {
    MultDimArray arrays = new MultDimArray();
    MultDimArray actual = serializeAndDeserialize(arrays);
    assertNull(actual.a2);
    assertNull(actual.a3);
    assertNull(actual.a4);
  }

  public void test_multidimensional_arrays_empty() {
    MultDimArray arrays = new MultDimArray();
    arrays.a2 = new short[][] { {} };
    arrays.a3 = new String[][][] { { {} } };
    arrays.a4 = new int[][][][] { { { {} } } };

    MultDimArray actual = serializeAndDeserialize(arrays);
    assertTrue(Arrays.deepEquals(arrays.a2, actual.a2));
    assertTrue(Arrays.deepEquals(arrays.a3, actual.a3));
    assertTrue(Arrays.deepEquals(arrays.a4, actual.a4));
  }

  public void test_multidimensional_array_double_referenced_sub_array() {
    int[][] array = new int[3][];
    array[0] = new int[] { 1, 2, 3 };
    array[1] = new int[] { 9, 1, 192, 1 };
    array[2] = array[0];

    int[][] actual = serializeAndDeserialize(array);
    assertTrue(Arrays.deepEquals(array, actual));
    assertSame(actual[0], actual[2]);
  }

  public void test_multidimensional_arrays_filled() {
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

  public void test_serialize_inner_class() {
    OuterClass outer = new OuterClass();
    OuterClass.InnerClass inner = outer.new InnerClass();
    OuterClass.InnerClass actual = serializeAndDeserialize(inner);
    assertEquals(inner.name, actual.name);
    assertEquals(inner.value, actual.value);
    assertEquals(inner.getClass(), actual.getClass());
  }
  
  public void test_serialize_inner_class2() {
    OuterClass2 outer = new OuterClass2();
    Object inner = outer.createInner();
    Object actual = serializeAndDeserialize(inner);
    assertNotNull(actual);
  }

  public void test_serialize_anonymous_class() {
    NamedClass named = new NamedClass();
    NamedClass actual = serializeAndDeserialize(named);
    assertEquals(named.anonymous, actual.anonymous);
  }

  public void test_same_map_values_are_not_duplicated() {
    Map<Key, Value> map = new HashMap<Key, Value>();

    Value v1 = new Value("A", 1);
    Value v2 = new Value("B", 2);

    map.put(new Key(19), v1);
    map.put(new Key(93), v2);
    map.put(new Key(11), v1);

    Map<Key, Value> actual = serializeAndDeserialize(map);
    assertEquals(map, actual);
    assertSame(actual.get(new Key(19)), actual.get(new Key(11)));
  }

  public void test_serialible_interface() {
    SerializeMe s = new SerializeMe();
    SerializeMe actual = serializeAndDeserialize(s);
    assertTrue(s.fWriteCalled);
    assertTrue(actual.fReadCalled);
  }

  public void test_serialize_date() {
    Date date = new Date();
    Date actual = serializeAndDeserialize(date);
    assertEquals(date, actual);
  }

  static class SetKey {

    private final HashSet<Key> fKeys;

    public SetKey(Key... keySet) {
      fKeys = new HashSet<Key>();
      fKeys.addAll(Arrays.asList(keySet));
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + fKeys.hashCode();
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final SetKey other = (SetKey) obj;
      return fKeys.equals(other.fKeys);
    }


  }

  public void test_map_with_set_key() {
    Map<SetKey, Value> map = new HashMap<SetKey, Value>();
    Value v1 = new Value("foo", 44.4);
    map.put(new SetKey(new Key(12), new Key(841), new Key(11)), v1);
    Value v2 = new Value("empty", 0.01);
    map.put(new SetKey(), v2);
    Value v3 = new Value("two", 22);
    map.put(new SetKey(new Key(1), new Key(2)), v3);

    Map<SetKey, Value> actual = serializeAndDeserialize(map);
    assertEquals(3, actual.size());
    assertEquals(v1, map.get(new SetKey(new Key(12), new Key(841), new Key(11))));
    assertEquals(v2, map.get(new SetKey()));
    assertEquals(v3, map.get(new SetKey(new Key(1), new Key(2))));
  }

}
