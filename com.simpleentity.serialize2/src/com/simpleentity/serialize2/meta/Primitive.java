package com.simpleentity.serialize2.meta;

import org.povworld.collection.Map;
import org.povworld.collection.mutable.HashMap;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.entity.id.EntityId;

public enum Primitive {

	BYTE(BootStrap.ID_PRIMITIVE_BYTE, Byte.class), CHAR(BootStrap.ID_PRIMITIVE_CHAR, Character.class), SHORT(
			BootStrap.ID_PRIMITIVE_SHORT, Short.class), INT(BootStrap.ID_PRIMITIVE_INT, Integer.class), LONG(
			BootStrap.ID_PRIMITIVE_LONG, Long.class), FLOAT(BootStrap.ID_PRIMITIVE_FLOAT, Float.class), STRING(
			BootStrap.ID_PRIMITIVE_STRING, String.class), DOUBLE(BootStrap.ID_PRIMITIVE_DOUBLE, Double.class);

	private final EntityId entityId;
	private Class<?> type;

	private Primitive(EntityId entityId, Class<?> type) {
		this.entityId = entityId;
		this.type = type;
	}

	public EntityId getEntityId() {
		return entityId;
	}

	public long getId() {
		return entityId.getId();
	}

	private Class<?> getType() {
		return type;
	}

	// TODO Create PerfectHashMap.
	private static final Map<EntityId, Primitive> entityId2Primitive;
	private static final Map<Class<?>, Primitive> type2Primitive;
	static {
		HashMap<EntityId, Primitive> byEntityId = new HashMap<>(values().length);
		HashMap<Class<?>, Primitive> byType = new HashMap<>(values().length);
		for (Primitive primitive : values()) {
			byEntityId.put(primitive.entityId, primitive);
			byType.put(primitive.getType(), primitive);
		}
		entityId2Primitive = byEntityId;
		type2Primitive = byType;
	}

	public static Primitive byEntityId(EntityId id) {
		Primitive result = entityId2Primitive.get(id);
		if (result == null) {
			throw new IllegalArgumentException("EntityId " + id + " does not correspond to any primitive!");
		}
		return result;
	}

	@CheckForNull
	public static Primitive byTypeOrNull(Class<?> type) {
		return type2Primitive.get(type);
	}
}
