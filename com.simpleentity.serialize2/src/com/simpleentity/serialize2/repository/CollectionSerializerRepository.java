package com.simpleentity.serialize2.repository;

import javax.annotation.CheckForNull;

import org.povworld.collection.mutable.HashMap;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.collection.ArraySerializer;
import com.simpleentity.serialize2.collection.CollectionSerializer;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaDataRepository;

// TODO threadafe?
public class CollectionSerializerRepository {

	private final HashMap<EntityId, CollectionSerializer<?>> collectionSerializers = new HashMap<>();

	public CollectionSerializerRepository() {
	}

	public void registerCollectionSerializer(EntityId metaDataId, CollectionSerializer<?> serializer) {
		collectionSerializers.put(metaDataId, serializer);
	}

	@CheckForNull
	public CollectionSerializer<?> getCollectionSerializer(EntityId metaDataId) {
		return collectionSerializers.get(metaDataId);
	}

	public void registerBootstrapSerializers(MetaDataRepository metaDataRepository, ClassLoader classLoader) {
		ArraySerializer arraySerializer = new ArraySerializer(metaDataRepository, classLoader);
		collectionSerializers.put(BootStrap.ID_PRIMITIVE_ARRAY, arraySerializer);
		collectionSerializers.put(BootStrap.ID_OBJECT_ARRAY, arraySerializer);
		collectionSerializers.put(BootStrap.ID_MULTI_DIMENSIONAL_ARRAY, arraySerializer);
	}

}
