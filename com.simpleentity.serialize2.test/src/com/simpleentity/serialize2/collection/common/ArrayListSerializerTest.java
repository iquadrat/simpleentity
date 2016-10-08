package com.simpleentity.serialize2.collection.common;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.povworld.collection.List;
import org.povworld.collection.immutable.ImmutableCollections;
import org.povworld.collection.mutable.ArrayList;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.collection.CollectionInfo;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaDataRepository;
import com.simpleentity.serialize2.meta.Type;
import com.simpleentity.testmodel.Date;
import com.simpleentity.testmodel.Song;

@RunWith(MockitoJUnitRunner.class)
public class ArrayListSerializerTest {

	private static final EntityId ARRAY_LIST_ID = new EntityId(123);

	@Mock
	private MetaDataRepository metaDataRepository;

	private ArrayListSerializer serializer;

	@Before
	public void setUp() {
		serializer = new ArrayListSerializer(ARRAY_LIST_ID, metaDataRepository);
	}

	@Test
	public void testEmptyList() {
		CollectionInfo serialized = serializer.serialize(new ArrayList<>());
		assertEquals(
				new CollectionInfo(
					ObjectInfo.newBuilder().setMetaDataId(ARRAY_LIST_ID).build(),
					BootStrap.ID_ANY,
					ImmutableCollections.asList()),
				serialized);
		assertEquals(new ArrayList<>(), serializer.deserialize(serialized));
	}

	@Test
	public void testNonEmptyList() {
		ArrayList<Object> list = new ArrayList<>();
		list.pushAll(ImmutableCollections.asList("foo", 12, new Date(2012, 10, 21)));
		CollectionInfo serialized = serializer.serialize(list);
		assertEquals(
				new CollectionInfo(
					ObjectInfo.newBuilder().setMetaDataId(ARRAY_LIST_ID).build(),
					BootStrap.ID_ANY,
					ImmutableCollections.asList("foo", 12, new Date(2012, 10, 21))),
				serialized);
		assertEquals(list, serializer.deserialize(serialized));
	}


	static class Multi<X extends Song & Iterable<?>, Y extends Song, Z extends List<? extends List<? super Song>>> {
		List<Object> untyped;
		List<Song> typedElement;
		ArrayList<Object> typedList;
		List<Song> typedBoth;
		List<? extends Song> extendsElement;
		List<?> wildcard;
		List<X> complex;
		List<Z> complexer;
		List<Y> indirect;
		List<? super Song> superElement;
		List<? extends List<Song>> nested;
	}

	private static final EntityId ID_SONG = new EntityId(8271);
	private static final EntityId ID_LIST = new EntityId(9999);

	@Test
	public void getElementType() throws Exception {
		Mockito.when(metaDataRepository.getMetaDataId(Object.class)).thenReturn(BootStrap.ID_ANY);
		Mockito.when(metaDataRepository.getMetaDataId(Song.class)).thenReturn(ID_SONG);
		Mockito.when(metaDataRepository.getMetaDataId(List.class)).thenReturn(ID_LIST);

		assertEquals(Type.required(BootStrap.ID_ANY), serializer.getElementType(Multi.class.getDeclaredField("untyped")));
		assertEquals(Type.required(ID_SONG), serializer.getElementType(Multi.class.getDeclaredField("typedElement")));
		assertEquals(Type.required(BootStrap.ID_ANY), serializer.getElementType(Multi.class.getDeclaredField("typedList")));
		assertEquals(Type.required(ID_SONG), serializer.getElementType(Multi.class.getDeclaredField("typedBoth")));
		assertEquals(Type.required(ID_SONG), serializer.getElementType(Multi.class.getDeclaredField("extendsElement")));
		assertEquals(Type.required(BootStrap.ID_ANY), serializer.getElementType(Multi.class.getDeclaredField("wildcard")));
		assertEquals(Type.required(ID_SONG), serializer.getElementType(Multi.class.getDeclaredField("indirect")));
		assertEquals(Type.required(BootStrap.ID_ANY), serializer.getElementType(Multi.class.getDeclaredField("superElement")));
		assertEquals(Type.required(ID_LIST), serializer.getElementType(Multi.class.getDeclaredField("nested")));
		assertEquals(Type.required(ID_LIST), serializer.getElementType(Multi.class.getDeclaredField("complexer")));
	}

	@Test(expected = SerializerException.class)
	public void getElementTypeOfComplexExtendsFails() throws Exception {
		serializer.getElementType(Multi.class.getDeclaredField("complex"));
	}
}
