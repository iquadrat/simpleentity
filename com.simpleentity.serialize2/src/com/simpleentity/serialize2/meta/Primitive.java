package com.simpleentity.serialize2.meta;

import org.povworld.collection.Map;
import org.povworld.collection.mutable.HashMap;

import com.simpleentity.entity.id.EntityId;

public enum Primitive {

	BOOLEAN(BootStrap.ID_PRIMITIVE_BOOLEAN, boolean.class, Boolean.class),
	BYTE(BootStrap.ID_PRIMITIVE_BYTE, byte.class, Byte.class),
	CHAR(BootStrap.ID_PRIMITIVE_CHAR, char.class, Character.class),
	SHORT(BootStrap.ID_PRIMITIVE_SHORT, short.class, Short.class),
	INT(BootStrap.ID_PRIMITIVE_INT, int.class, Integer.class),
	LONG(BootStrap.ID_PRIMITIVE_LONG, long.class, Long.class),
	VARINT(BootStrap.ID_PRIMITIVE_VARINT, long.class, Long.class),
	FLOAT(BootStrap.ID_PRIMITIVE_FLOAT, float.class, Float.class),
	DOUBLE(BootStrap.ID_PRIMITIVE_DOUBLE, double.class, Double.class),
	STRING(BootStrap.ID_PRIMITIVE_STRING, String.class, String.class);

	private final EntityId metaDataId;
	private final Class<?> boxedType;
	private final Class<?> type;

	private Primitive(EntityId metaDataId, Class<?> type, Class<?> boxedType) {
		this.metaDataId = metaDataId;
		this.type = type;
		this.boxedType = boxedType;
	}

	public long getId() {
		return metaDataId.getId();
	}

	public Class<?> getType() {
		return type;
	}

	public Class<?> getBoxedType() {
		return boxedType;
	}

	public EntityId getMetaDataId() {
		return metaDataId;
	}

	public MetaData getMetaData() {
		return MetaData.newBuilder()
				.setClassName(getBoxedType().getName())
				.setDomain(BootStrap.PRIMITIVE_DOMAIN)
				.setMetaType(MetaType.PRIMITIVE)
				.setVersion(BootStrap.SIMPLE_ENTITY_VERSION)
				.build(getMetaDataId());
	}

	// TODO Create PerfectHashMap.
	private static final Map<EntityId, Primitive> entityId2Primitive;
	static {
		HashMap<EntityId, Primitive> byEntityId = new HashMap<>(values().length);
		HashMap<Class<?>, Primitive> byType = new HashMap<>(values().length);
		for (Primitive primitive : values()) {
			byEntityId.put(primitive.metaDataId, primitive);
			byType.put(primitive.getType(), primitive);
		}
		entityId2Primitive = byEntityId;
	}

	public static Primitive byEntityId(EntityId id) {
		Primitive result = entityId2Primitive.get(id);
		if (result == null) {
			throw new IllegalArgumentException("EntityId " + id + " does not correspond to any primitive!");
		}
		return result;
	}
}
