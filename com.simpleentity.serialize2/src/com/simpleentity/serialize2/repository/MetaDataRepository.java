package com.simpleentity.serialize2.repository;

import java.util.HashMap;

import net.jcip.annotations.NotThreadSafe;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.id.EntityIdFactory;
import com.simpleentity.serialize2.SerializerRepository;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.MetaData;
import com.simpleentity.serialize2.meta.MetaDataFactory;
import com.simpleentity.serialize2.meta.MetaDataUtil;
import com.simpleentity.serialize2.meta.Primitive;

// TODO should this be persistent?
@NotThreadSafe
public class MetaDataRepository {

	private final HashMap<EntityId, MetaData> id2MetaData = new HashMap<>();
	private final HashMap<Class<?>, EntityId> class2id = new HashMap<>();

	@CheckForNull
	private final MetaDataFactory metaDataFactory;
	@CheckForNull
	private final EntityIdFactory idFactory;
	private final SerializerRepository serializerRepository;

	public MetaDataRepository(MetaDataFactory metaDataFactory, EntityIdFactory idFactory,
			SerializerRepository serializerRepository) {
		this.metaDataFactory = metaDataFactory;
		this.idFactory = idFactory;
		this.serializerRepository = serializerRepository;
		registerBootstrapMetaData();
	}

	private void registerBootstrapMetaData() {
		add(EntityId.class, BootStrap.ENTITY_ID);
		add(Object.class, BootStrap.ANY);
		for(Primitive primitive: Primitive.values()) {
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

	public MetaData getMetaData(EntityId metaDataId) {
		MetaData metaData = id2MetaData.get(metaDataId);
		if (metaData != null) {
			return metaData;
		}
		throw missing(MetaData.class, metaDataId);
	}

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

	public MetaData getMetaData(Class<?> class_) {
		return getMetaData(getMetaDataId(class_));
	}

	private MetaData createMetaData(Class<?> class_) {
		if (metaDataFactory == null) {
			throw missing(MetaData.class, class_.getName());
		}
		EntityId id = idFactory.newEntityId();
		class2id.put(class_, id);
		MetaData metaData = metaDataFactory.create(class_, id, serializerRepository);
		id2MetaData.put(metaData.getEntityId(), metaData);
		return metaData;
	}

	private IllegalArgumentException missing(Class<?> type, Object id) {
		return new IllegalArgumentException("No " + type.getSimpleName() + " for id '" + id + "' found!");
	}

}
