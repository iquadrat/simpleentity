package com.simpleentity.serialize2.integration;

import org.junit.Before;
import org.junit.Test;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaDataTestUtil;
import com.simpleentity.serialize2.meta.MetaType;
import com.simpleentity.serialize2.meta.Type;
import com.simpleentity.util.bytes.ByteChunk;

public class MetaDataSerializationTest extends AbstractSerializationTest {

	private static final String TEST_DOMAIN = "test.a.domain";
	private static final long TEST_VERSION = 42;
	private MetaData.Builder builder;

	@Before
	public void setUp() {
		builder = MetaData.newBuilder()
				.setDomain(TEST_DOMAIN)
				.setClassName(TEST_DOMAIN + ".Test")
				.setVersion(TEST_VERSION);
	}

	@Override
	protected <T> T deserialize(ByteChunk bytes) {
		// Reset repository to make sure no MetaData objects outside of
		// BootStrap are required.
		serializerRepository = createSerializerRepository();
		return super.deserialize(bytes);
	}

	@Test
	public void serializeMinimalMetaData() {
		MetaData metaData = builder
				.setMetaType(MetaType.ENTITY)
				.build(idFactory);
		serializeAndDeserialize(metaData);
	}

	@Test
	public void serializeBootStrapMetaData() {
		serializeAndDeserialize(BootStrap.ENTITY_ID);
		serializeAndDeserialize(BootStrap.META_TYPE);
		serializeAndDeserialize(BootStrap.TYPE);
		serializeAndDeserialize(BootStrap.ENTRY);
		serializeAndDeserialize(BootStrap.META_DATA);
		serializeAndDeserialize(BootStrap.ANY);
		serializeAndDeserialize(BootStrap.PRIMITIVE_ARRAY);
		serializeAndDeserialize(BootStrap.OBJECT_ARRAY);
		serializeAndDeserialize(BootStrap.MULTI_DIMENSIONAL_ARRAY);
	}

	@Test
	public void serializeSingleEntryMetaData() {
		MetaData metaData = builder
				.setMetaType(MetaType.VALUE_OBJECT)
				.addEntry("foo", new Type(BootStrap.ID_PRIMITIVE_INT, true))
				.build(idFactory);
		serializeAndDeserialize(metaData);
	}

	@Test
	public void serializeMultiEntryMetaData() {
		EntityId customMetaDataId = new EntityId(123456);
		MetaData metaData = builder
				.setMetaType(MetaType.VALUE_OBJECT)
				.addEntry("required", new Type(BootStrap.ID_PRIMITIVE_INT, true))
				.addEntry("optional", new Type(BootStrap.ID_PRIMITIVE_INT, false))
				.addEntry("custom", new Type(customMetaDataId, false))
				.build(idFactory);
		serializeAndDeserialize(metaData);
	}

	@Test
	public void serializeCollectionEntryMetaData() {
		MetaData metaData = builder
				.setMetaType(MetaType.VALUE_OBJECT)
				.addEntry("collection", new Type(BootStrap.ID_PRIMITIVE_ARRAY, false, new Type(BootStrap.ID_PRIMITIVE_INT, false)))
				.addEntry("optional", new Type(BootStrap.ID_OBJECT_ARRAY, true, new Type(BootStrap.ID_PRIMITIVE_VARINT, true)))
				.build(idFactory);
		serializeAndDeserialize(metaData);
	}

	@Test
	public void serializeNestedCollectionEntryMetaData() {
		MetaData metaData = builder
				.setMetaType(MetaType.VALUE_OBJECT)
				.addEntry("nested",
						new Type(BootStrap.ID_MULTI_DIMENSIONAL_ARRAY, false,
								new Type(BootStrap.ID_PRIMITIVE_ARRAY, true,
									new Type(BootStrap.ID_PRIMITIVE_ARRAY, true,
										new Type(BootStrap.ID_PRIMITIVE_INT, false)))))
				.build(idFactory);
		serializeAndDeserialize(metaData);
	}

	private void serializeAndDeserialize(MetaData metaData) {
		MetaData actual = super.serializeAndDeserialize(metaData);
		MetaDataTestUtil.assertDeepEquals(metaData, actual);
	}

}
