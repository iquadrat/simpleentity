package com.simpleentity.serialize2.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.annotation.CheckForNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.simpleentity.entity.Entity;
import com.simpleentity.entity.EntityBuilder;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.value.ValueObject;
import com.simpleentity.serialize2.MockIdFactory;
import com.simpleentity.serialize2.Serializer;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.GenericValue.EntityIdValue;
import com.simpleentity.serialize2.generic.GenericValue.PrimitiveValue;
import com.simpleentity.serialize2.generic.GenericValue.ValueObjectValue;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaData.Builder;
import com.simpleentity.serialize2.meta.MetaDataUtil;
import com.simpleentity.serialize2.meta.MetaType;
import com.simpleentity.serialize2.meta.Primitive;

/** Unit tests for {@link JavaSerializer}. */
@RunWith(MockitoJUnitRunner.class)
public class JavaSerializerTest {

	private static final String TEST_DOMAIN = JavaSerializerTest.class.getPackage().getName();
	private static final long TEST_VERSION = 4;
	private static final EntityId TEST_ENTITY_ID = new EntityId(1000L);

	@Mock private JavaSerializationContext context;
	@Mock private SerializerRepository serializerRepository;
	private final MockIdFactory idFactory = new MockIdFactory();
	private final Instantiator instantiator = new ObjenesisInstantiator();

	@Before
	public void setUp() {
		Mockito.when(context.getClassLoader()).thenReturn(getClass().getClassLoader());
		Mockito.when(context.getSerializerRepository()).thenReturn(serializerRepository);
		Mockito.when(context.getInstantiator()).thenReturn(instantiator);
		Mockito.when(serializerRepository.getMetaDataId(EntityId.class)).thenReturn(BootStrap.ID_ENTITY_ID);
		Mockito.when(serializerRepository.getMetaData(EntityId.class)).thenReturn(BootStrap.ENTITY_ID);
		for(Primitive primitive: Primitive.values()) {
			Mockito.when(serializerRepository.getMetaDataId(primitive.getType())).thenReturn(primitive.getMetaDataId());
			Mockito.when(serializerRepository.getMetaDataId(primitive.getBoxedType())).thenReturn(primitive.getMetaDataId());
			Mockito.when(serializerRepository.getMetaData(primitive.getType())).thenReturn(primitive.getMetaData());
			Mockito.when(serializerRepository.getMetaData(primitive.getBoxedType())).thenReturn(primitive.getMetaData());
		}
	}

	private static class EntityEmpty extends Entity<EntityEmpty> {
		EntityEmpty(EntityId id) {
			super(id);
		}

		@Override
		public EntityBuilder<EntityEmpty> toBuilder() {
			throw new UnsupportedOperationException();
		}
	}

	@Test
	public void serializeEmptyEntity() {
		MetaData metaData = prepareEntityMetaData(EntityEmpty.class);
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
			.setMetaDataId(metaData.getEntityId())
			.setEntryValue(Entity.ID_FIELD_NAME, new GenericValue.EntityIdValue(TEST_ENTITY_ID))
			.build();

		ObjectInfo actual = serialize(EntityEmpty.class, new EntityEmpty(TEST_ENTITY_ID));
		assertEquals(objectInfo, actual);

		EntityEmpty entity = deserialize(EntityEmpty.class, objectInfo);
		assertEquals(TEST_ENTITY_ID, entity.getEntityId());
	}

	private static class EntityPrimitives extends Entity<EntityPrimitives> {
		private final boolean b;
		private final char c;
		private final byte y;
		private final short s;
		private final int i;
		private final long l;
		private final float f;
		private final double d;

		private final Boolean B;
		private final Byte Y;
		private final Character C;
		private final Short S;
		private final Integer I;
		private final Long L;
		private final Float F;
		private final Double D;

		private final String string;
		// TODO add optional field

		EntityPrimitives(EntityId id, boolean b, boolean B, char c, char C, byte y, byte Y, short s, short S, int i, int I, long l, long L, float f, float F, double d, double D, String string) {
			super(id);
			this.b = b;
			this.B = B;
			this.c = c;
			this.C = C;
			this.y = y;
			this.Y = Y;
			this.s = s;
			this.S = S;
			this.i = i;
			this.I = I;
			this.l = l;
			this.L = L;
			this.f = f;
			this.F= F;
			this.d = d;
			this.D = D;
			this.string = string;
		}

		@Override
		public EntityBuilder<EntityPrimitives> toBuilder() {
			throw new UnsupportedOperationException();
		}
	}

	@Test
	public void serializeEmptyPrimitives() {
		MetaData metaData = prepareEntityMetaData(EntityPrimitives.class);
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
			.setMetaDataId(metaData.getEntityId())
			.setEntryValue(Entity.ID_FIELD_NAME, new GenericValue.EntityIdValue(TEST_ENTITY_ID))
			.setEntryValue("b", new PrimitiveValue(Primitive.BOOLEAN, true))
			.setEntryValue("B", new PrimitiveValue(Primitive.BOOLEAN, false))
			.setEntryValue("c", new PrimitiveValue(Primitive.CHAR, 'a'))
			.setEntryValue("C", new PrimitiveValue(Primitive.CHAR, 'b'))
			.setEntryValue("y", new PrimitiveValue(Primitive.BYTE, (byte)-4))
			.setEntryValue("Y", new PrimitiveValue(Primitive.BYTE, (byte)3))
			.setEntryValue("s", new PrimitiveValue(Primitive.SHORT, (short)256))
			.setEntryValue("S", new PrimitiveValue(Primitive.SHORT, (short)257))
			.setEntryValue("i", new PrimitiveValue(Primitive.INT, 50000))
			.setEntryValue("I", new PrimitiveValue(Primitive.INT, 60000))
			.setEntryValue("l", new PrimitiveValue(Primitive.LONG, 700000000L))
			.setEntryValue("L", new PrimitiveValue(Primitive.LONG, 800000000L))
			.setEntryValue("f", new PrimitiveValue(Primitive.FLOAT, 9.9f))
			.setEntryValue("F", new PrimitiveValue(Primitive.FLOAT, 10.1f))
			.setEntryValue("d", new PrimitiveValue(Primitive.DOUBLE, 11.11))
			.setEntryValue("D", new PrimitiveValue(Primitive.DOUBLE, 12.12))
			.setEntryValue("string", new PrimitiveValue(Primitive.STRING, "foo"))
			.build();

		ObjectInfo actual = serialize(EntityPrimitives.class,
				new EntityPrimitives(TEST_ENTITY_ID,
						true, false,
						'a', 'b',
						(byte)-4, (byte)3,
						(short)256, (short)257,
						50000, 60000,
						700000000L,
						800000000L,
						9.9f, 10.1f, 11.11, 12.12, "foo"));
		assertEquals(objectInfo, actual);

		EntityPrimitives entity = deserialize(EntityPrimitives.class, objectInfo);
		assertEquals(TEST_ENTITY_ID, entity.getEntityId());
		assertEquals(true, entity.b);
		assertEquals(false, entity.B);
		assertEquals('a', entity.c);
		assertEquals(Character.valueOf('b'), entity.C);
		assertEquals((byte)-4, entity.y);
		assertEquals(Byte.valueOf((byte)3), entity.Y);
		assertEquals((short)256, entity.s);
		assertEquals(Short.valueOf((short)257), entity.S);
		assertEquals(50000, entity.i);
		assertEquals(Integer.valueOf(60000), entity.I);
		assertEquals(700000000L, entity.l);
		assertEquals(Long.valueOf(800000000L), entity.L);
		assertEquals(9.9f, entity.f, 0);
		assertEquals(10.1f, entity.F, 0);
		assertEquals(11.11, entity.d, 0);
		assertEquals(12.12, entity.D, 0);
		assertEquals("foo", entity.string);
	}

	@SuppressWarnings("unused")
	private static class Value extends ValueObject {
		final int number;
		final @CheckForNull Value nested;
		public Value(int number, Value nested) {
			this.number = number;
			this.nested = nested;
		}
	}

	private static class EntityWithValueObject extends Entity<EntityWithValueObject> {
		private final Value foo;

		EntityWithValueObject(EntityId id, Value foo) {
			super(id);
			this.foo = foo;
		}

		@Override
		public EntityBuilder<EntityWithValueObject> toBuilder() {
			throw new UnsupportedOperationException();
		}
	}

	@Test
	public void serializeEntityWithValueObject() {
		MetaData valueMetaData = prepareValueObjectMetaData(Value.class);
		MetaData entityMetaData = prepareEntityMetaData(EntityWithValueObject.class);
		Value nested = new Value(43, null);
		Value value = new Value(42, nested);
		ObjectInfo valueInfo = ObjectInfo.newBuilder()
				.setMetaDataId(valueMetaData.getEntityId())
				.setEntryValue("number", new PrimitiveValue(Primitive.INT, 42))
				.setEntryValue("nested", new ValueObjectValue(valueMetaData.getEntityId(),
						ObjectInfo.newBuilder()
							.setMetaDataId(valueMetaData.getEntityId())
							.setEntryValue("number", new PrimitiveValue(Primitive.INT, 43))
							.build()))
				.build();
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
				.setMetaDataId(entityMetaData.getEntityId())
				.setEntryValue(Entity.ID_FIELD_NAME, new GenericValue.EntityIdValue(TEST_ENTITY_ID))
				.setEntryValue("foo", new ValueObjectValue(valueMetaData.getEntityId(), valueInfo))
				.build();
		ObjectInfo actual = serialize(EntityWithValueObject.class,
				new EntityWithValueObject(TEST_ENTITY_ID, value));
		assertEquals(objectInfo, actual);

		EntityWithValueObject entity = deserialize(EntityWithValueObject.class, objectInfo);
		assertEquals(TEST_ENTITY_ID, entity.getEntityId());
		assertEquals(value, entity.foo);
	}

	@SuppressWarnings("unused")
	private static class AbstractValue extends ValueObject {
		final int base;
		AbstractValue(int base) {
			this.base = base;
		}
	}

	private static class ValueA extends AbstractValue {
		@CheckForNull final String a;
		ValueA(int base, @CheckForNull String a) {
			super(base);
			this.a = a;
		}
	}

	@SuppressWarnings("unused")
	private static class ValueB extends AbstractValue {
		final double b;
		ValueB(int base, double b) {
			super(base);
			this.b = b;
		}
	}

	private static class EntityWithPolymorphicObjects extends Entity<EntityWithPolymorphicObjects> {
		final AbstractValue value;
		final ValueB b;
		final Object object;
		final @CheckForNull Object primitive;
		final Object entity;
		final Object value2;

		protected EntityWithPolymorphicObjects(EntityId id, AbstractValue value, ValueB b, Object object, Integer primitive, EntityId entity, AbstractValue value2) {
			super(id);
			this.value = value;
			this.b = b;
			this.object = object;
			this.primitive = primitive;
			this.entity = entity;
			this.value2 = value2;
		}

		@Override
		public EntityBuilder<EntityWithPolymorphicObjects> toBuilder() {
			throw new UnsupportedOperationException();
		}
	}

	@Test
	public void serializeEntityWithPolymorphicObjects() {
		MetaData objectMetaData = prepareValueObjectMetaData(Object.class);
		prepareValueObjectMetaData(AbstractValue.class);
		MetaData valueAMetaData = prepareValueObjectMetaData(ValueA.class);
		MetaData valueBMetaData = prepareValueObjectMetaData(ValueB.class);
		MetaData entityMetaData = prepareEntityMetaData(EntityWithPolymorphicObjects.class);

		EntityId other = new EntityId(1234);

		ObjectInfo object = ObjectInfo.newBuilder()
				.setMetaDataId(objectMetaData.getEntityId())
				.build();
		ObjectInfo valueAInfo = ObjectInfo.newBuilder()
				.setMetaDataId(valueAMetaData.getEntityId())
				.setEntryValue("base", new PrimitiveValue(Primitive.INT, 12))
				.setEntryValue("a", new PrimitiveValue(Primitive.STRING, "bar"))
				.build();
		ObjectInfo valueBInfo = ObjectInfo.newBuilder()
				.setMetaDataId(valueBMetaData.getEntityId())
				.setEntryValue("base", new PrimitiveValue(Primitive.INT, 13))
				.setEntryValue("b", new PrimitiveValue(Primitive.DOUBLE, Double.POSITIVE_INFINITY))
				.build();
		ObjectInfo value2Info = ObjectInfo.newBuilder()
				.setMetaDataId(valueAMetaData.getEntityId())
				.setEntryValue("base", new PrimitiveValue(Primitive.INT, 14))
				.build();
		ObjectInfo entityInfo = ObjectInfo.newBuilder()
				.setMetaDataId(entityMetaData.getEntityId())
				.setEntryValue(Entity.ID_FIELD_NAME, new GenericValue.EntityIdValue(TEST_ENTITY_ID))
				.setEntryValue("value", new ValueObjectValue(valueAMetaData.getEntityId(), valueAInfo))
				.setEntryValue("b", new ValueObjectValue(valueBMetaData.getEntityId(), valueBInfo))
				.setEntryValue("object", new ValueObjectValue(objectMetaData.getEntityId(), object))
				.setEntryValue("primitive", new PrimitiveValue(Primitive.INT, Integer.MAX_VALUE))
				.setEntryValue("entity", new EntityIdValue(other))
				.setEntryValue("value2", new ValueObjectValue(valueAMetaData.getEntityId(), value2Info))
				.build();

		ValueA valueA = new ValueA(12, "bar");
		ValueB valueB = new ValueB(13, Double.POSITIVE_INFINITY);
		ValueA value2 = new ValueA(14, null);
		ObjectInfo actual = serialize(EntityWithPolymorphicObjects.class, new EntityWithPolymorphicObjects(
				TEST_ENTITY_ID, valueA, valueB, new Object(),
				Integer.MAX_VALUE, other, value2));
		assertEquals(entityInfo, actual);

		EntityWithPolymorphicObjects entity = deserialize(EntityWithPolymorphicObjects.class, entityInfo);
		assertEquals(TEST_ENTITY_ID, entity.getEntityId());
		assertEquals(valueA, entity.value);
		assertEquals(valueB, entity.b);
		assertTrue(entity.object instanceof Object);
		assertEquals(Integer.MAX_VALUE, entity.primitive);
		assertEquals(other, entity.entity);
		assertEquals(value2, entity.value2);
	}

	private static class EntityWithReference extends Entity<EntityWithReference> {
		final EntityId another;
		final @CheckForNull EntityId optional;
		final @CheckForNull EntityId missing;
		public EntityWithReference(EntityId id, EntityId another, EntityId optional) {
			super(id);
			this.another = another;
			this.optional = optional;
			this.missing = null;
		}
		@Override
		public EntityBuilder<EntityWithReference> toBuilder() {
			throw new UnsupportedOperationException();
		}
	}

	@Test
	public void serializeEntityWithReferences() {
		MetaData entityMetaData = prepareEntityMetaData(EntityWithReference.class);
		EntityId otherId = new EntityId(1010);
		EntityId optionalId = new EntityId(1011);
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
				.setMetaDataId(entityMetaData.getEntityId())
				.setEntryValue(Entity.ID_FIELD_NAME, new GenericValue.EntityIdValue(TEST_ENTITY_ID))
				.setEntryValue("another", new EntityIdValue(otherId))
				.setEntryValue("optional", new EntityIdValue(optionalId))
				.build();

		ObjectInfo actual = serialize(EntityWithReference.class,
				new EntityWithReference(TEST_ENTITY_ID, otherId, optionalId));
		assertEquals(objectInfo, actual);

		EntityWithReference entity = deserialize(EntityWithReference.class, objectInfo);
		assertEquals(TEST_ENTITY_ID, entity.getEntityId());
		assertEquals(otherId, entity.another);
		assertEquals(optionalId, entity.optional);
	}

	@Test
	public void serializeEntityWithMultiFields() {
		// TODO
	}

	private <T> ObjectInfo serialize(Class<T> class_, T object) {
		Serializer<T> serializer =  new JavaSerializer<T>(context, class_);
		return serializer.serialize(object);
	}

	private <T> T deserialize(Class<T> class_, ObjectInfo objectInfo) {
		Serializer<T> serializer = new JavaSerializer<>(context, class_);
		return serializer.deserialize(objectInfo);
	}

	private MetaData prepareEntityMetaData(Class<?> class_) {
		return prepareMetaData(class_, MetaType.ENTITY);
	}

	private <T> MetaData prepareValueObjectMetaData(Class<T> class_) {
		MetaData result = prepareMetaData(class_, MetaType.VALUE_OBJECT);
		Mockito.doReturn(new JavaSerializer<T>(context, class_))
			   .when(serializerRepository).getSerializer(result.getEntityId());
		return result;
	}

	private MetaData prepareMetaData(Class<?> class_, MetaType metaType) {
		Mockito.when(serializerRepository.getMetaDataId(class_)).thenReturn(new EntityId(idFactory.nextId.get()));
		Builder builder = MetaData.newBuilder()
			.setClassName(class_.getName())
			.setDomain(class_.getName().startsWith("java.lang") ? "java.lang" : TEST_DOMAIN)
			.setVersion(TEST_VERSION)
			.setMetaType(metaType)
			.build(idFactory)
			.toBuilder();
		MetaDataUtil.addFieldsToMetaDataEntries(class_, serializerRepository, builder);
		MetaData result = builder.build(idFactory);
		Mockito.when(serializerRepository.getMetaData(class_)).thenReturn(result);
		return result;
	}
}