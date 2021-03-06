package com.simpleentity.serialize2.repository;

import java.lang.reflect.Field;

import net.jcip.annotations.ThreadSafe;

import org.povworld.collection.mutable.HashMap;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.Instantiator;
import com.simpleentity.serialize2.SerializationContext;
import com.simpleentity.serialize2.Serializer;
import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.binary.BinarySerializer;
import com.simpleentity.serialize2.binary.BinarySerializerRepository;
import com.simpleentity.serialize2.binary.EntityIdSerializer;
import com.simpleentity.serialize2.binary.ObjectInfoSerializer;
import com.simpleentity.serialize2.binary.PrimitiveSerializer;
import com.simpleentity.serialize2.collection.CollectionSerializer;
import com.simpleentity.serialize2.generic.ObjectInfo;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaDataRepository;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.serialize2.meta.Type;
import com.simpleentity.serialize2.reflect.ReflectiveSerializer;

@ThreadSafe
public class JavaSerializerRepository implements BinarySerializerRepository, SerializerRepository, MetaDataRepository {

	private final Object lock = new Object();
	private final HashMap<EntityId, Serializer<?>> serializers = new HashMap<>();
	private final BinarySerializer<EntityId> entityIdSerializer = new EntityIdSerializer();

	private final ClassLoader classLoader;

	private final SerializationContext context;
	private final MetaDataRepository metaDataRepository;
	private final CollectionSerializerRepository collectionSerializerRepository;

	public JavaSerializerRepository(CollectionSerializerRepository collectionSerializerRepository, MetaDataRepository metaDataRepository, ClassLoader classLoader, Instantiator instantiator) {
		this.collectionSerializerRepository = collectionSerializerRepository;
		this.classLoader = classLoader;
		this.context = new SerializationContext(classLoader, this, this, instantiator);
		this.metaDataRepository = metaDataRepository;
		collectionSerializerRepository.registerBootstrapSerializers(metaDataRepository, classLoader);
	}

	@Override
	public MetaData getMetaData(EntityId metaDataId) {
		synchronized (lock) {
			return  metaDataRepository.getMetaData(metaDataId);
		}
	}

	@Override
	public EntityId getMetaDataId(Class<?> class_) {
		synchronized (lock) {
			return metaDataRepository.getMetaDataId(class_);
		}
	}

	@Override
	public MetaData getMetaData(Class<?> class_) {
		synchronized (lock) {
			return metaDataRepository.getMetaData(class_);
		}
	}

	@Override
	public Type getDeclaredType(Field field) {
		synchronized (lock) {
			return metaDataRepository.getDeclaredType(field);
		}
	}

	@Override
	public Serializer<?> getSerializer(EntityId metaDataId) {
		synchronized (lock) {
			Serializer<?> serializer = serializers.get(metaDataId);
			if (serializer != null) {
				return serializer;
			}
			return createSerializer(metaDataId);
		}
	}

	private Serializer<?> createSerializer(EntityId metaDataId) {
		MetaData metaData = metaDataRepository.getMetaData(metaDataId);
		// TODO cache? or always store in map
		Class<?> class_;
		try {
			class_ = classLoader.loadClass(metaData.getClassName());
		} catch (ClassNotFoundException e) {
			throw new SerializerException(e);
		}
		ReflectiveSerializer<?> serializer = new ReflectiveSerializer<>(class_, metaData, context);
		serializers.put(metaDataId, serializer);
		return serializer;
	}

	@Override
	public CollectionSerializer<?> getCollectionSerializer(EntityId metaDataId) {
		synchronized (lock) {
			CollectionSerializer<?> serializer = collectionSerializerRepository.getCollectionSerializer(metaDataId);
			if (serializer != null) {
				return serializer;
			}
			throw missing(CollectionSerializer.class, metaDataId);
		}
	}

	private IllegalArgumentException missing(Class<?> type, Object id) {
		return new IllegalArgumentException("No " + type.getSimpleName() + " for id '" + id + "' found!");
	}

	@SuppressWarnings("unchecked")
	@Override
	public BinarySerializer<Object> getPrimitiveSerializer(EntityId metaDataId) {
		return (BinarySerializer<Object>) PrimitiveSerializer.getSerializer(Primitive.byEntityId(metaDataId));
	}

	@Override
	public BinarySerializer<EntityId> getEntityIdSerializer() {
		return entityIdSerializer;
	}

	@Override
	public BinarySerializer<ObjectInfo> getBinarySerializer(EntityId metaDataId) {
		// TODO cache
		return new ObjectInfoSerializer(this, getMetaData(metaDataId));
	}

}
