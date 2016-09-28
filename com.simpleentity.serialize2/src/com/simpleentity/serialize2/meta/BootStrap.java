package com.simpleentity.serialize2.meta;

import com.simpleentity.entity.id.EntityId;

public class BootStrap {

	public static final String SIMPLE_ENTITY_DOMAIN = "com.simpleentity";
	public static final long SIMPLE_ENTITY_VERSION = 1;
	public static final String PRIMITIVE_DOMAIN = "java.lang";

	public static final EntityId ID_NULL_REFERENCE = new EntityId(0);

	public static final EntityId ID_ENTITIY_ID = new EntityId(1);
	public static final EntityId ID_META_DATA = new EntityId(2);

	private static final long ID_PRIMITIVE_FIRST = 16;
	public static final EntityId ID_PRIMITIVE_BOOLEAN = new EntityId(ID_PRIMITIVE_FIRST + 0);
	public static final EntityId ID_PRIMITIVE_BYTE = new EntityId(ID_PRIMITIVE_FIRST + 1);
	public static final EntityId ID_PRIMITIVE_CHAR = new EntityId(ID_PRIMITIVE_FIRST + 2);
	public static final EntityId ID_PRIMITIVE_SHORT = new EntityId(ID_PRIMITIVE_FIRST + 3);
	public static final EntityId ID_PRIMITIVE_INT = new EntityId(ID_PRIMITIVE_FIRST + 4);
	public static final EntityId ID_PRIMITIVE_LONG = new EntityId(ID_PRIMITIVE_FIRST + 5);
	public static final EntityId ID_PRIMITIVE_FLOAT = new EntityId(ID_PRIMITIVE_FIRST + 6);
	public static final EntityId ID_PRIMITIVE_DOUBLE = new EntityId(ID_PRIMITIVE_FIRST + 7);
	public static final EntityId ID_PRIMITIVE_STRING = new EntityId(ID_PRIMITIVE_FIRST + 8);
	private static final long ID_PRIMITIVE_LAST = ID_PRIMITIVE_STRING.getId();

	public static final EntityId ID_CARDINALITY_ONE = new EntityId(32);
	public static final EntityId ID_CARDINALITY_OPTIONAL = new EntityId(33);
	public static final EntityId ID_CARDINALITY_ANY = new EntityId(34);

	public static boolean isPrimitive(EntityId entityId) {
		return (entityId.getId() >= ID_PRIMITIVE_FIRST) && (entityId.getId() <= ID_PRIMITIVE_LAST);
	}

	public static final Cardinality CARDINALITY_ONE;
	public static final Cardinality CARDINALITY_OPTIONAL;
	public static final Cardinality CARDINALITY_ANY;

	public static final MetaData ENTITY_ID;

	static {
		CARDINALITY_ONE = Cardinality.newBuilder().setMin(1).setMax(1).build(ID_CARDINALITY_ONE);
		CARDINALITY_OPTIONAL = Cardinality.newBuilder().setMin(0).setMax(1).build(ID_CARDINALITY_OPTIONAL);
		CARDINALITY_ANY = Cardinality.newBuilder().setMin(0).setMax(Long.MAX_VALUE).build(ID_CARDINALITY_ANY);
		ENTITY_ID = MetaData.newBuilder()
				.setClassName(EntityId.class.getName())
				.setDomain(SIMPLE_ENTITY_DOMAIN)
				.setMetaType(MetaType.ENTITY)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.build(ID_ENTITIY_ID);
	}

}
