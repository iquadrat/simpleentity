package com.simpleentity.serialize2.binary;

import org.povworld.collection.immutable.ImmutableArrayList;
import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.BinarySerializer;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.generic.ObjectInfo.Builder;
import com.simpleentity.serialize2.meta.Cardinality;
import com.simpleentity.serialize2.meta.Entry;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

public class ObjectInfoSerializer implements BinarySerializer<ObjectInfo> {

	private final BinarySerializer<EntityId> entityIdSerialzer;
	private final ImmutableList<EntrySerializer> entrySerializers;

	@Override
	public Class<ObjectInfo> getType() {
		return ObjectInfo.class;
	}

	public ObjectInfoSerializer(SerializerRepository serializerRepository, MetaData metaData) {
		this.entityIdSerialzer = serializerRepository.getEntityIdSerializer();
		this.entrySerializers = createFieldSerializers(serializerRepository, metaData);
	}

	private static ImmutableList<EntrySerializer> createFieldSerializers(
			SerializerRepository serializerRepository, MetaData metaData) {
		ImmutableArrayList.Builder<EntrySerializer> result = ImmutableArrayList.newBuilder();
		for (Entry entry : metaData.getEntries()) {
			result.add(createFieldSerializer(serializerRepository, entry));
		}
		return result.build();
	}

	private static EntrySerializer createFieldSerializer(SerializerRepository serializerRepository, Entry entry) {
		Cardinality cardinality = serializerRepository.getCardinality(entry.getCardinality());
		MetaData declaredType = serializerRepository.getMetaData(entry.getDeclaredTypeId());
		return new EntrySerializer(entry.getId(), cardinality, declaredType, serializerRepository);
	}

	@Override
	public void serialize(ObjectInfo objectInfo, ByteWriter destination) {
		entityIdSerialzer.serialize(objectInfo.getMetaTypeId(), destination);
		for (EntrySerializer serializer : entrySerializers) {
			serializer.serialize(objectInfo, destination);
		}
	}

	@Override
	public ObjectInfo deserialize(ByteReader source) {
		Builder builder = ObjectInfo.newBuilder();
		builder.setMetaDataId(entityIdSerialzer.deserialize(source));
		for (EntrySerializer serializer : entrySerializers) {
			serializer.deserialize(source, builder);
		}
		return builder.build();
	}
}
