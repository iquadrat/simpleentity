package com.simpleentity.serialize2.binary;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.BinarySerializer;
import com.simpleentity.serialize2.MockIdFactory;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.GenericValue.ValueObjectValue;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaType;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.serialize2.meta.Type;
import com.simpleentity.util.bytes.ByteChunk;
import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

@RunWith(MockitoJUnitRunner.class)
public class ObjectInfoSerializerTest {

	private static final EntityId TEST_METADATA_ID = new EntityId(256);
	private static final String TEST_DOMAIN = "test.domain";
	private static final String TEST_CLASS = TEST_DOMAIN + ".Test";
	private static final long TEST_VERSION = 42;

	@Mock
	SerializerRepository repository;

	private final MockIdFactory idFactory = new MockIdFactory();

	private MetaData metaDataBase;

	@Before
	public void setUp() {
		Mockito.when(repository.getEntityIdSerializer()).thenReturn(new EntityIdSerializer());
		for(Primitive primitive: Primitive.values()) {
			BinarySerializer<?> serializer = PrimitiveSerializer.getSerializer(primitive);
			Mockito.doReturn(serializer).when(repository).getPrimitiveSerializer(primitive.getMetaDataId());
			Mockito.when(repository.getMetaData(primitive.getMetaDataId())).thenReturn(primitive.getMetaData());
		}

		metaDataBase = MetaData.newBuilder()
				.setDomain(TEST_DOMAIN)
				.setClassName(TEST_CLASS)
				.setMetaType(MetaType.ENTITY)
				.setVersion(TEST_VERSION)
				.build(new MockIdFactory(TEST_METADATA_ID));
	}

	@Test
	public void serializeEmptyEntityInfo() {
		MetaData metaData = metaDataBase.toBuilder()
				.build(idFactory);
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
			.setMetaDataId(TEST_METADATA_ID)
			.build();
		serializeAndDeserialize(objectInfo, metaData, "");
	}

	@Test
	public void serializePrimitives() {
		MetaData metaData = metaDataBase.toBuilder()
				.addEntry("t", new Type(BootStrap.ID_PRIMITIVE_BOOLEAN, false))
				.addEntry("b", new Type(BootStrap.ID_PRIMITIVE_BYTE, false))
				.addEntry("c", new Type(BootStrap.ID_PRIMITIVE_CHAR, false))
				.addEntry("d", new Type(BootStrap.ID_PRIMITIVE_DOUBLE, false))
				.addEntry("f", new Type(BootStrap.ID_PRIMITIVE_FLOAT, false))
				.addEntry("i", new Type(BootStrap.ID_PRIMITIVE_INT, false))
				.addEntry("l", new Type(BootStrap.ID_PRIMITIVE_LONG, false))
				.addEntry("s", new Type(BootStrap.ID_PRIMITIVE_SHORT, false))
				.addEntry("str", new Type(BootStrap.ID_PRIMITIVE_STRING, false))
				.addEntry("var", new Type(BootStrap.ID_PRIMITIVE_VARINT, false))
				.build(idFactory);
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
				.setMetaDataId(TEST_METADATA_ID)
				.setEntryValue("b", GenericValue.byteValue((byte)0x42))
				.setEntryValue("c", GenericValue.charValue('x'))
				.setEntryValue("d", GenericValue.doubleValue(11.11))
				.setEntryValue("f", GenericValue.floatValue(12.12f))
				.setEntryValue("i", GenericValue.intValue(0x12345678))
				.setEntryValue("l", GenericValue.longValue(0xedcba9876543210L))
				.setEntryValue("s", GenericValue.shortValue((short)0x2244))
				.setEntryValue("str", GenericValue.stringValue("Hello world!"))
				.setEntryValue("t", GenericValue.booleanValue(true))
				.setEntryValue("var", GenericValue.varIntValue(0x1111))
				.build();

		serializeAndDeserialize(objectInfo, metaData,
				"42" + "7800" /* 'x' */ + "b81e85eb51382640" /* 11.11 */+ "85eb4141" /* 12.12.f */+
				"78563412" + "1032547698badc0e" + "4422" + "8c48656c6c6f20776f726c6421" /* (varint)12 "Hello world!" */ +
				"01" + "5111");
	}

	@Test
	public void serializeOptionalPrimitives() {
		MetaData metaData = metaDataBase.toBuilder()
				.addEntry("maybe", new Type(BootStrap.ID_PRIMITIVE_INT, true))
				.addEntry("missing", new Type(BootStrap.ID_PRIMITIVE_INT, true))
				.build(idFactory);
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
				.setMetaDataId(TEST_METADATA_ID)
				.setEntryValue("maybe", GenericValue.intValue(0x42))
				.build();
		serializeAndDeserialize(objectInfo, metaData,
				"8142000000" + "80");
	}

	@Test
	public void serializeValueObject() {
		MetaData value = MetaData.newBuilder()
				.setDomain(TEST_DOMAIN)
				.setVersion(TEST_VERSION)
				.setClassName(TEST_DOMAIN+".Value")
				.setMetaType(MetaType.VALUE_OBJECT)
				.addEntry("int", new Type(BootStrap.ID_PRIMITIVE_INT, false))
				.build(new MockIdFactory(0x1aa));
		MetaData metaData = metaDataBase.toBuilder()
				.addEntry("0value", new Type(value.getEntityId(), false))
				.addEntry("1maybe", new Type(value.getEntityId(), true))
				.addEntry("2missing", new Type(value.getEntityId(), true))
				.build(idFactory);
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
				.setMetaDataId(TEST_METADATA_ID)
				.setEntryValue("0value", new ValueObjectValue(ObjectInfo.newBuilder()
						.setMetaDataId(value.getEntityId())
						.setEntryValue("int", GenericValue.intValue(0x77))
						.build()))
				.setEntryValue("1maybe", new ValueObjectValue(ObjectInfo.newBuilder()
						.setMetaDataId(value.getEntityId())
						.setEntryValue("int", GenericValue.intValue(0x88))
						.build()))
				.build();
		ObjectInfoSerializer valueSerializer = new ObjectInfoSerializer(repository, value);
		when(repository.getBinarySerializer(value.getEntityId())).thenReturn(valueSerializer);
		when(repository.getMetaData(value.getEntityId())).thenReturn(value);
		serializeAndDeserialize(objectInfo, metaData,
				"41aa77000000" + "41aa88000000" + "80");
	}

	private void serializeAndDeserialize(ObjectInfo objectInfo, MetaData metaData, String expectedBytes) {
		ObjectInfoSerializer serializer = new ObjectInfoSerializer(repository, metaData);
		ByteWriter actualBytes = new ByteWriter();
		serializer.serialize(objectInfo, actualBytes);
		assertEquals(ByteChunk.fromHexString(expectedBytes), actualBytes.build());
		ObjectInfo actualInfo = serializer.deserialize(new ByteReader(ByteChunk.fromHexString(expectedBytes)));
		assertEquals(objectInfo, actualInfo);
	}

}

