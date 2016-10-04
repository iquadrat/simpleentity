package com.simpleentity.serialize2.binary;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.povworld.collection.immutable.ImmutableCollections;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.BinarySerializer;
import com.simpleentity.serialize2.MockIdFactory;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.generic.GenericValue;
import com.simpleentity.serialize2.generic.GenericValue.CollectionValue;
import com.simpleentity.serialize2.generic.GenericValue.EntityIdValue;
import com.simpleentity.serialize2.generic.GenericValue.PrimitiveValue;
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
		when(repository.getMetaData(BootStrap.ID_ENTITY_ID)).thenReturn(BootStrap.ENTITY_ID);
		when(repository.getMetaData(BootStrap.ID_ANY)).thenReturn(BootStrap.ANY);
		when(repository.getMetaData(BootStrap.ID_ARRAY)).thenReturn(BootStrap.ARRAY);
		for(Primitive primitive: Primitive.values()) {
			BinarySerializer<?> serializer = PrimitiveSerializer.getSerializer(primitive);
			Mockito.doReturn(serializer).when(repository).getPrimitiveSerializer(primitive.getMetaDataId());
			Mockito.when(repository.getMetaData(primitive.getMetaDataId())).thenReturn(primitive.getMetaData());
		}
		when(repository.getEntityIdSerializer()).thenReturn(new EntityIdSerializer());
		ObjectInfoSerializer arraySerializer = new ObjectInfoSerializer(repository, BootStrap.ARRAY);
		when(repository.getBinarySerializer(BootStrap.ID_ARRAY)).thenReturn(arraySerializer);

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

	@Test
	public void serializeEntityReference() {
		EntityId ref1 = new EntityId(257);
		EntityId ref2 = new EntityId(258);
		MetaData metaData = metaDataBase.toBuilder()
				.addEntry("0ref", new Type(BootStrap.ID_ENTITY_ID, false))
				.addEntry("1maybe", new Type(BootStrap.ID_ENTITY_ID, true))
				.addEntry("2missing", new Type(BootStrap.ID_ENTITY_ID, true))
				.build(idFactory);
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
				.setMetaDataId(TEST_METADATA_ID)
				.setEntryValue("0ref", new EntityIdValue(ref1))
				.setEntryValue("1maybe", new EntityIdValue(ref2))
				.build();
		serializeAndDeserialize(objectInfo, metaData,
				"4101" + "4102" + "80");
	}

	@Test
	public void serializeUnknownMetaType() {
		EntityId ref1 = new EntityId(257);
		MetaData metaData = metaDataBase.toBuilder()
				.addEntry("0primitive", new Type(BootStrap.ID_ANY, true))
				.addEntry("1entity", new Type(BootStrap.ID_ANY, false))
				.addEntry("2missing", new Type(BootStrap.ID_ANY, true))
				.build(idFactory);
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
				.setMetaDataId(TEST_METADATA_ID)
				.setEntryValue("0primitive", new PrimitiveValue(Primitive.INT, 0x77))
				.setEntryValue("1entity", new EntityIdValue(ref1))
				.build();
		serializeAndDeserialize(objectInfo, metaData,
				"9477000000" + "814101" + "80");
	}

	@Test
	public void serializeCollection() {
		EntityId e1 = new EntityId(0x333);
		EntityId e2 = new EntityId(0x334);
		EntityId e3 = new EntityId(0x335);

		EntityId collectionId = new EntityId(0x1aa);
		MetaData collection = MetaData.newBuilder()
				.setDomain(TEST_DOMAIN)
				.setVersion(TEST_VERSION)
				.setClassName(TEST_DOMAIN+".Collection")
				.setMetaType(MetaType.COLLECTION)
				.addEntry("cap", new Type(BootStrap.ID_PRIMITIVE_VARINT, false))
				.build(new MockIdFactory(collectionId));
		MetaData metaData = metaDataBase.toBuilder()
				.addEntry("0Ints", new Type(BootStrap.ID_ARRAY, false, new Type(BootStrap.ID_PRIMITIVE_INT, false)))
				.addEntry("1IntsOrNot", new Type(BootStrap.ID_ARRAY, false, new Type(BootStrap.ID_PRIMITIVE_INT, true)))
				.addEntry("2Ids", new Type(collectionId, true, new Type(BootStrap.ID_ENTITY_ID, false)))
				.addEntry("3anys", new Type(collectionId, true, new Type(BootStrap.ID_ANY, true)))
				.addEntry("99missing", new Type(collectionId, true, new Type(BootStrap.ID_ANY, true)))
				.build(idFactory);
		ObjectInfo.Builder intsInfo = ObjectInfo.newBuilder()
				.setMetaDataId(BootStrap.ID_ARRAY)
				.setEntryValue("componentType", GenericValue.stringValue("java.lang.Integer"))
				.setEntryValue("dimension", GenericValue.varIntValue(1));
		ObjectInfo collectionInfo = ObjectInfo.newBuilder()
				.setMetaDataId(collectionId)
				.setEntryValue("cap", GenericValue.varIntValue(0x17))
				.build();
		ObjectInfo objectInfo = ObjectInfo.newBuilder()
				.setMetaDataId(TEST_METADATA_ID)
				.setEntryValue(
						"0Ints",
						new CollectionValue(intsInfo.setEntryValue("length", GenericValue.varIntValue(4)).build(), ImmutableCollections.<GenericValue> asList(
								GenericValue.intValue(1), GenericValue.intValue(2), GenericValue.intValue(3),
								GenericValue.intValue(5))))
				.setEntryValue("1IntsOrNot", new CollectionValue( intsInfo.setEntryValue("length", GenericValue.varIntValue(2)).build(), ImmutableCollections.<GenericValue> asList(
						GenericValue.intValue(0x12345678), GenericValue.intValue(0x42))))
				.setEntryValue("2Ids", new CollectionValue(collectionInfo, ImmutableCollections.<GenericValue>asList(
						new GenericValue.EntityIdValue(e1), new GenericValue.EntityIdValue(e2), new GenericValue.EntityIdValue(e3))))
				.setEntryValue("3anys", new CollectionValue(collectionInfo, ImmutableCollections.<GenericValue>asList(
						GenericValue.intValue(0x1234), GenericValue.booleanValue(true), new GenericValue.EntityIdValue(e2))))
				.build();
		when(repository.getMetaData(collectionId)).thenReturn(collection);
		ObjectInfoSerializer collectionSerializer = new ObjectInfoSerializer(repository, collection);
		when(repository.getBinarySerializer(collectionId)).thenReturn(collectionSerializer);

		serializeAndDeserialize(objectInfo, metaData,
				"a0" + "916a6176612e6c616e672e496e7465676572" /* componentType */+
				"81" /* dimension */+ "84" /* length */+ "8401000000020000000300000005000000" +
				"a0" + "916a6176612e6c616e672e496e7465676572" /* componentType */+
				"81" /* dimension */+ "82" /* length */+ "82"+ "8178563412" + "8142000000" +
				"41aa" + "97" /* cap */ + "83" + "433343344335" +
				"41aa" + "97" /* cap */ + "83" + "9434120000" + "9001" + "814334" +
				"80"
				);
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

