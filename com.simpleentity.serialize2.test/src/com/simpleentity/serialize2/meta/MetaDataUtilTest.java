package com.simpleentity.serialize2.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.CheckForNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.povworld.collection.List;

import com.simpleentity.annotation.Positive;
import com.simpleentity.annotation.ValueObject;
import com.simpleentity.entity.id.AtomicIdFactory;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.reflect.ReflectiveMetaDataFactory;
import com.simpleentity.serialize2.repository.CollectionSerializerRepository;
import com.simpleentity.serialize2.repository.JavaMetaDataRepository;
import com.simpleentity.testmodel.Artist;
import com.simpleentity.testmodel.Asset;
import com.simpleentity.testmodel.Book;
import com.simpleentity.testmodel.Date;
import com.simpleentity.testmodel.Sex;
import com.simpleentity.testmodel.Song;

@RunWith(MockitoJUnitRunner.class)
public class MetaDataUtilTest {

	private MetaDataFactory metaDataFactory;
	private MetaDataRepository metaDataRepository;
	private CollectionSerializerRepository collectionSerializerRepository;

	@Before
	public void setUp() {
		AtomicIdFactory idFactory = new AtomicIdFactory(BootStrap.ID_RANGE_END + 1);
		metaDataFactory = new ReflectiveMetaDataFactory();
		collectionSerializerRepository = new CollectionSerializerRepository();
		metaDataRepository = new JavaMetaDataRepository(metaDataFactory, idFactory, collectionSerializerRepository);
	}

	class PositiveOrSigned {
		@Positive
		int positive;
		int signed;
	}

	@Test
	public void isPositive() throws Exception {
		assertTrue(MetaDataUtil.isPositive(PositiveOrSigned.class.getDeclaredField("positive")));
		assertFalse(MetaDataUtil.isPositive(PositiveOrSigned.class.getDeclaredField("signed")));
	}

	class OptionalOrRequired {
		@CheckForNull
		Integer optional;
		Integer required;
	}

	@Test
	public void isOptional() throws Exception {
		assertTrue(MetaDataUtil.isOptional(OptionalOrRequired.class.getDeclaredField("optional")));
		assertFalse(MetaDataUtil.isOptional(OptionalOrRequired.class.getDeclaredField("required")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayMetaDataIdNonArrayThrows() {
		MetaDataUtil.getArrayMetaDataId(Object.class);
	}

	@Test
	public void getArrayMetaDataId() {
		assertEquals(BootStrap.ID_PRIMITIVE_ARRAY, MetaDataUtil.getArrayMetaDataId(int[].class));
		assertEquals(BootStrap.ID_OBJECT_ARRAY, MetaDataUtil.getArrayMetaDataId(Integer[].class));
		assertEquals(BootStrap.ID_OBJECT_ARRAY, MetaDataUtil.getArrayMetaDataId(Object[].class));
		assertEquals(BootStrap.ID_MULTI_DIMENSIONAL_ARRAY, MetaDataUtil.getArrayMetaDataId(float[][].class));
		assertEquals(BootStrap.ID_MULTI_DIMENSIONAL_ARRAY, MetaDataUtil.getArrayMetaDataId(Object[][].class));
		assertEquals(BootStrap.ID_MULTI_DIMENSIONAL_ARRAY, MetaDataUtil.getArrayMetaDataId(Object[][][].class));
	}

	@ValueObject
	class SomeValue {}

	@Test
	public void tryGetMetaType() {
		assertEquals(MetaType.PRIMITIVE, MetaDataUtil.tryGetMetaType(int.class));
		assertEquals(MetaType.VALUE_OBJECT, MetaDataUtil.tryGetMetaType(Song.class));
		assertEquals(MetaType.VALUE_OBJECT, MetaDataUtil.tryGetMetaType(SomeValue.class));
		assertEquals(MetaType.ENTITY, MetaDataUtil.tryGetMetaType(Asset.class));
		assertEquals(MetaType.ENTITY, MetaDataUtil.tryGetMetaType(Artist.class));
		assertEquals(MetaType.ENTITY, MetaDataUtil.tryGetMetaType(Book.class));
		assertEquals(MetaType.ENUM, MetaDataUtil.tryGetMetaType(Sex.class));
	}

	@Test
	public void tryGetMetaType_Undecisive() {
		assertNull(MetaDataUtil.tryGetMetaType(Integer.class));
		assertNull(MetaDataUtil.tryGetMetaType(Object.class));
		assertNull(MetaDataUtil.tryGetMetaType(String.class));
		assertNull(MetaDataUtil.tryGetMetaType(List.class));
		assertNull(MetaDataUtil.tryGetMetaType(Iterable.class));
	}

	private static final String TEST_MODEL_DOMAIN = "com.simpleentity.testmodel";
	private static final long TEST_MODEL_VERSION = 1;

	private static final EntityId TEST_ID = new EntityId(1234567);

	@Test
	public void getMetaDataByReflectionValueObject() {
		MetaData actual = MetaDataUtil.getMetaDataByReflection(
				Date.class, TEST_MODEL_DOMAIN, TEST_MODEL_VERSION, TEST_ID, metaDataRepository);
		MetaData expected = MetaData.newBuilder()
				.setDomain(TEST_MODEL_DOMAIN)
				.setVersion(TEST_MODEL_VERSION)
				.setClassName(TEST_MODEL_DOMAIN + ".Date")
				.setMetaType(MetaType.VALUE_OBJECT)
				.addEntry("day", Type.required(BootStrap.ID_PRIMITIVE_VARINT))
				.addEntry("month", Type.required(BootStrap.ID_PRIMITIVE_VARINT))
				.addEntry("year", Type.required(BootStrap.ID_PRIMITIVE_INT))
				.build(TEST_ID);
		MetaDataTestUtil.assertDeepEquals(expected, actual);
	}



}
