package com.simpleentity.serialize2.repository;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.jcip.annotations.NotThreadSafe;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.id.EntityIdFactory;
import com.simpleentity.serialize2.collection.CollectionSerializer;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.Entry;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaDataFactory;
import com.simpleentity.serialize2.meta.MetaDataRepository;
import com.simpleentity.serialize2.meta.MetaDataUtil;
import com.simpleentity.serialize2.meta.MetaType;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.serialize2.meta.Type;

// TODO should this be persistent?
@NotThreadSafe
public class JavaMetaDataRepository implements MetaDataRepository {

	private final HashMap<EntityId, MetaData> id2MetaData = new HashMap<>();
	private final HashMap<Class<?>, EntityId> class2id = new HashMap<>();

	@CheckForNull
	private final MetaDataFactory metaDataFactory;
	@CheckForNull
	private final EntityIdFactory idFactory;
	private final CollectionSerializerRepository collectionSerializerRepository;

	public JavaMetaDataRepository(MetaDataFactory metaDataFactory, EntityIdFactory idFactory,
			CollectionSerializerRepository collectionSerializerRepository) {
		this.metaDataFactory = metaDataFactory;
		this.idFactory = idFactory;
		this.collectionSerializerRepository = collectionSerializerRepository;
		registerBootstrapMetaData();
	}

	private void registerBootstrapMetaData() {
		add(EntityId.class, BootStrap.ENTITY_ID);
		add(Object.class, BootStrap.ANY);
		add(MetaType.class, BootStrap.META_TYPE);
		add(Type.class, BootStrap.TYPE);
		add(Entry.class, BootStrap.ENTRY);
		add(MetaData.class, BootStrap.META_DATA);
		for (Primitive primitive : Primitive.values()) {
			if (primitive == Primitive.VARINT) {
				// TODO we need to handle varints somehow though
				continue;
			}
			add(primitive.getType(), primitive.getMetaData());
			add(primitive.getBoxedType(), primitive.getMetaData());
		}
		id2MetaData.put(BootStrap.ID_PRIMITIVE_VARINT, Primitive.VARINT.getMetaData());
		id2MetaData.put(BootStrap.ID_PRIMITIVE_ARRAY, BootStrap.PRIMITIVE_ARRAY);
		id2MetaData.put(BootStrap.ID_OBJECT_ARRAY, BootStrap.OBJECT_ARRAY);
		id2MetaData.put(BootStrap.ID_MULTI_DIMENSIONAL_ARRAY, BootStrap.MULTI_DIMENSIONAL_ARRAY);
	}

	public void add(Class<?> type, MetaData metaData) {
		class2id.put(type, metaData.getEntityId());
		id2MetaData.put(metaData.getEntityId(), metaData);
	}

	@Override
	public MetaData getMetaData(EntityId metaDataId) {
		MetaData metaData = id2MetaData.get(metaDataId);
		if (metaData != null) {
			return metaData;
		}
		throw missing(MetaData.class, metaDataId);
	}

	@Override
	public EntityId getMetaDataId(Class<?> class_) {
		if (class_.isArray()) {
			return MetaDataUtil.getArrayMetaDataId(class_);
		}
		EntityId metaDataId = class2id.get(class_);
		if (metaDataId != null) {
			return metaDataId;
		}
		return createMetaData(class_).getEntityId();
	}

	@Override
	public MetaData getMetaData(Class<?> class_) {
		return getMetaData(getMetaDataId(class_));
	}

	private MetaData createMetaData(Class<?> class_) {
		if (metaDataFactory == null) {
			throw missing(MetaData.class, class_.getName());
		}
		EntityId id = idFactory.newEntityId();
		class2id.put(class_, id);
		MetaData metaData = metaDataFactory.create(class_, id, this);
		id2MetaData.put(metaData.getEntityId(), metaData);
		return metaData;
	}

	private IllegalArgumentException missing(Class<?> type, Object id) {
		return new IllegalArgumentException("No " + type.getSimpleName() + " for id '" + id + "' found!");
	}

	@Override
	public Type getDeclaredType(Field field) {
		Class<?> type = field.getType();
		boolean optional = MetaDataUtil.isOptional(field);
		EntityId metaDataId = getMetaDataId(type);
		if (BootStrap.isPrimitive(metaDataId) && MetaDataUtil.isPositive(field)) {
			// TODO this is an ugly hack
			metaDataId = BootStrap.ID_PRIMITIVE_VARINT;
		}
		CollectionSerializer<?> collectionSerializer = collectionSerializerRepository
				.getCollectionSerializer(metaDataId);
		Type elementType = (collectionSerializer == null) ? null : collectionSerializer.getElementType(field);
		return new Type(metaDataId, optional, elementType);
	}

}
