package com.simpleentity.serialize2.binary;

import org.povworld.collection.EntryIterator;
import org.povworld.collection.immutable.ImmutableArrayList;
import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.serialize2.BinarySerializer;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.generic.ObjectInfo.Builder;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.Type;
import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

public class ObjectInfoSerializer implements BinarySerializer<ObjectInfo> {

	private final MetaData metaData;
	private final ImmutableList<EntrySerializer> entrySerializers;

	@Override
	public Class<ObjectInfo> getType() {
		return ObjectInfo.class;
	}

	public ObjectInfoSerializer(SerializerRepository serializerRepository, MetaData metaData) {
		this.metaData = metaData;
		this.entrySerializers = createFieldSerializers(serializerRepository, metaData);
	}

	private static ImmutableList<EntrySerializer> createFieldSerializers(
			SerializerRepository serializerRepository, MetaData metaData) {
		ImmutableArrayList.Builder<EntrySerializer> result = ImmutableArrayList.newBuilder();
		EntryIterator<String, Type> entries = metaData.getEntries();
		while(entries.next()) {
			result.add(createFieldSerializer(serializerRepository, entries.getCurrentKey(), entries.getCurrentValue()));
		}
		return result.build();
	}

	private static EntrySerializer createFieldSerializer(SerializerRepository serializerRepository, String id, Type type) {
		return new EntrySerializer(id, type, serializerRepository);
	}

	@Override
	public void serialize(ObjectInfo objectInfo, ByteWriter destination) {
		for (EntrySerializer serializer : entrySerializers) {
			serializer.serialize(objectInfo, destination);
		}
	}

	@Override
	public ObjectInfo deserialize(ByteReader source) {
		Builder builder = ObjectInfo.newBuilder()
			.setMetaDataId(metaData.getEntityId());
		for (EntrySerializer serializer : entrySerializers) {
			serializer.deserialize(source, builder);
		}
		return builder.build();
	}
}
