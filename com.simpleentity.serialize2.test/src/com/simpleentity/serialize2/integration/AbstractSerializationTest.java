package com.simpleentity.serialize2.integration;

import org.junit.Before;

import com.simpleentity.entity.id.AtomicIdFactory;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.id.EntityIdFactory;
import com.simpleentity.serialize2.Serializer;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaDataFactory;
import com.simpleentity.serialize2.reflect.ObjenesisInstantiator;
import com.simpleentity.serialize2.reflect.ReflectiveMetaDataFactory;
import com.simpleentity.serialize2.repository.CollectionSerializerRepository;
import com.simpleentity.serialize2.repository.JavaMetaDataRepository;
import com.simpleentity.serialize2.repository.JavaSerializerRepository;
import com.simpleentity.util.bytes.ByteChunk;
import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

public class AbstractSerializationTest {

	private static final boolean PRINT_BYTES = true;

	protected EntityIdFactory idFactory;
	protected MetaDataFactory metaDataFactory;
	protected JavaSerializerRepository serializerRepository;

	@Before
	public void setUpBase() {
		idFactory = new AtomicIdFactory(BootStrap.ID_RANGE_END + 1);
		metaDataFactory = new ReflectiveMetaDataFactory();
		serializerRepository = createSerializerRepository();
	}

	protected JavaSerializerRepository createSerializerRepository() {
		CollectionSerializerRepository collectionSerializerRepository = new CollectionSerializerRepository();
		JavaMetaDataRepository metaDataRepository = new JavaMetaDataRepository(metaDataFactory, idFactory, collectionSerializerRepository);
		return new JavaSerializerRepository(collectionSerializerRepository, metaDataRepository,
				getClass().getClassLoader(), new ObjenesisInstantiator());
	}

	protected <T> T serializeAndDeserialize(T object) {
		ByteChunk bytes = serialize(object);
		if (PRINT_BYTES) {
			System.out.println(bytes);
		}
		return deserialize(bytes);
	}

	@SuppressWarnings("unchecked")
	protected <T> ByteChunk serialize(T entity) {
		EntityId metaDataId = serializerRepository.getMetaDataId(entity.getClass());
		Serializer<T> serializer = (Serializer<T>) serializerRepository.getSerializer(metaDataId);
		ObjectInfo objectInfo = serializer.serialize(entity);
		System.out.println(objectInfo);
		ByteWriter destination = new ByteWriter();
		destination.putPositiveVarInt(metaDataId.getId());
		serializerRepository.getBinarySerializer(metaDataId).serialize(objectInfo, destination);
		return destination.build();
	}

	@SuppressWarnings("unchecked")
	protected <T> T deserialize(ByteChunk bytes) {
		ByteReader source = new ByteReader(bytes);
		EntityId metaDataId = new EntityId(source.getPositiveVarInt());
		ObjectInfo objectInfo = serializerRepository.getBinarySerializer(metaDataId).deserialize(source);
		System.out.println(objectInfo);
		Serializer<T> serializer = (Serializer<T>) serializerRepository.getSerializer(metaDataId);
		return serializer.deserialize(objectInfo);
	}
}
