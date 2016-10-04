package com.simpleentity.serialize2.meta;

import com.simpleentity.entity.id.EntityId;

public class BootStrap {

	public static final String SIMPLE_ENTITY_DOMAIN = "com.simpleentity";
	public static final long SIMPLE_ENTITY_VERSION = 1;
	public static final String JAVA_DOMAIN = "java.lang";
	public static final String PRIMITIVE_DOMAIN = JAVA_DOMAIN;

	public static final EntityId ID_NULL_REFERENCE = new EntityId(0);

	public static final EntityId ID_ENTITY_ID = new EntityId(1);
	public static final EntityId ID_META_DATA = new EntityId(2);
	public static final EntityId ID_ANY = new EntityId(3);

	private static final long ID_PRIMITIVE_FIRST = 16;
	public static final EntityId ID_PRIMITIVE_BOOLEAN = new EntityId(ID_PRIMITIVE_FIRST + 0);
	public static final EntityId ID_PRIMITIVE_BYTE = new EntityId(ID_PRIMITIVE_FIRST + 1);
	public static final EntityId ID_PRIMITIVE_CHAR = new EntityId(ID_PRIMITIVE_FIRST + 2);
	public static final EntityId ID_PRIMITIVE_SHORT = new EntityId(ID_PRIMITIVE_FIRST + 3);
	public static final EntityId ID_PRIMITIVE_INT = new EntityId(ID_PRIMITIVE_FIRST + 4);
	public static final EntityId ID_PRIMITIVE_LONG = new EntityId(ID_PRIMITIVE_FIRST + 5);
	public static final EntityId ID_PRIMITIVE_VARINT = new EntityId(ID_PRIMITIVE_FIRST + 6);
	public static final EntityId ID_PRIMITIVE_FLOAT = new EntityId(ID_PRIMITIVE_FIRST + 7);
	public static final EntityId ID_PRIMITIVE_DOUBLE = new EntityId(ID_PRIMITIVE_FIRST + 8);
	public static final EntityId ID_PRIMITIVE_STRING = new EntityId(ID_PRIMITIVE_FIRST + 9);
	private static final long ID_PRIMITIVE_LAST = ID_PRIMITIVE_STRING.getId();

	public static final EntityId ID_ARRAY = new EntityId(32);

	public static boolean isPrimitive(EntityId entityId) {
		return (entityId.getId() >= ID_PRIMITIVE_FIRST) && (entityId.getId() <= ID_PRIMITIVE_LAST);
	}


	public static final MetaData ENTITY_ID;
	public static final MetaData ARRAY;
	public static final MetaData ANY;

	static {
		ENTITY_ID = MetaData.newBuilder()
				.setClassName(EntityId.class.getName())
				.setDomain(SIMPLE_ENTITY_DOMAIN)
				.setMetaType(MetaType.ENTITY)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.build(ID_ENTITY_ID);
		ARRAY = MetaData.newBuilder()
				.setClassName("java.lang.array")
				.setDomain(JAVA_DOMAIN)
				.setMetaType(MetaType.COLLECTION)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.addEntry("componentType", new Type(ID_PRIMITIVE_STRING, false))
				.addEntry("dimension", new Type(ID_PRIMITIVE_VARINT, false))
				.addEntry("length", new Type(ID_PRIMITIVE_VARINT, false))
				.build(ID_ARRAY);
		ANY = MetaData.newBuilder()
				.setClassName("java.lang.Object")
				.setDomain(JAVA_DOMAIN)
				.setMetaType(MetaType.UNKNOWN)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.build(ID_ANY);
	}

}
