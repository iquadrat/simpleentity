package com.simpleentity.serialize2.meta;

import com.simpleentity.entity.Entity;
import com.simpleentity.entity.id.EntityId;

public class BootStrap {

	public static final String SIMPLE_ENTITY_DOMAIN = "com.simpleentity";
	public static final long SIMPLE_ENTITY_VERSION = 1;
	public static final String JAVA_DOMAIN = "java.lang";
	public static final String PRIMITIVE_DOMAIN = "";

	public static final EntityId ID_NULL_REFERENCE = new EntityId(0);

	public static final EntityId ID_ENTITY_ID = new EntityId(1);
	public static final EntityId ID_ANY = new EntityId(3);

	public static final EntityId ID_META_TYPE = new EntityId(4);
	public static final EntityId ID_TYPE = new EntityId(6);
	public static final EntityId ID_ENTRY = new EntityId(5);
	public static final EntityId ID_META_DATA = new EntityId(2);

	// TODO change ids for array to be below primitives?
	public static final EntityId ID_PRIMITIVE_ARRAY = new EntityId(32);
	public static final EntityId ID_OBJECT_ARRAY = new EntityId(33);
	public static final EntityId ID_MULTI_DIMENSIONAL_ARRAY = new EntityId(34);

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

	public static final long ID_RANGE_END = 63;

	public static boolean isPrimitive(EntityId entityId) {
		return (entityId.getId() >= ID_PRIMITIVE_FIRST) && (entityId.getId() <= ID_PRIMITIVE_LAST);
	}

	public static final MetaData ENTITY_ID;

	public static final MetaData META_TYPE;
	public static final MetaData TYPE;
	public static final MetaData ENTRY;

	public static final String META_DATA_DOMAIN = "domain";
	public static final String META_DATA_VERSION = "version";
	public static final String META_DATA_META_TYPE = "metaType";
	public static final String META_DATA_CLASS_NAME = "className";
	public static final String META_DATA_ENTRIES = "entries";
	public static final MetaData META_DATA;

	public static final MetaData ANY;
	public static final MetaData PRIMITIVE_ARRAY;
	public static final MetaData OBJECT_ARRAY;

	public static final String MULTI_DIMENSIONAL_ARRAY_DIMENSIONS = "dimensions";
	public static final String MULTI_DIMENSIONAL_ARRAY_PRIMITIVE = "primitive";
	public static final String MULTI_DIMENSIONAL_ARRAY_LEAF_META_DATA = "leafMetaData";
	public static final MetaData MULTI_DIMENSIONAL_ARRAY;

	public static final String ENUM_ORDINAL = "ordinal";

	static {
		ENTITY_ID = MetaData.newBuilder()
				.setDomain(SIMPLE_ENTITY_DOMAIN)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.setClassName(EntityId.class.getName())
				.setMetaType(MetaType.ENTITY)
				.build(ID_ENTITY_ID);
		META_TYPE = MetaData.newBuilder()
				.setDomain(SIMPLE_ENTITY_DOMAIN)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.setClassName(MetaType.class.getName())
				.setMetaType(MetaType.ENUM)
				.build(ID_META_TYPE);
		TYPE = MetaData.newBuilder()
				.setDomain(SIMPLE_ENTITY_DOMAIN)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.setClassName(Type.class.getName())
				.setMetaType(MetaType.VALUE_OBJECT)
				.addEntry("metaDataId", new Type(ID_ENTITY_ID, false))
				.addEntry("optional", new Type(ID_PRIMITIVE_BOOLEAN, false))
				.addEntry("elementType", new Type(ID_TYPE, true))
				.build(ID_TYPE);
		ENTRY = MetaData.newBuilder()
				.setDomain(SIMPLE_ENTITY_DOMAIN)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.setClassName(Entry.class.getName())
				.setMetaType(MetaType.VALUE_OBJECT)
				.addEntry("id", new Type(ID_PRIMITIVE_STRING, false))
				.addEntry("type", new Type(ID_TYPE, false))
				.build(ID_ENTRY);
		META_DATA = MetaData.newBuilder()
				.setDomain(SIMPLE_ENTITY_DOMAIN)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.setClassName(MetaData.class.getName())
				.setMetaType(MetaType.ENTITY)
				.addEntry(Entity.ID_FIELD_NAME, new Type(ID_ENTITY_ID, false))
				.addEntry(META_DATA_DOMAIN, new Type(ID_PRIMITIVE_STRING, false))
				.addEntry(META_DATA_VERSION, new Type(ID_PRIMITIVE_VARINT, false))
				.addEntry(META_DATA_META_TYPE, new Type(ID_META_TYPE, false))
				.addEntry(META_DATA_CLASS_NAME, new Type(ID_PRIMITIVE_STRING, false))
				.addEntry(META_DATA_ENTRIES, new Type(ID_OBJECT_ARRAY, false, new Type(ID_ENTRY, false)))
				.build(ID_META_DATA);
		ANY = MetaData.newBuilder()
				.setClassName("java.lang.Object")
				.setDomain(JAVA_DOMAIN)
				.setMetaType(MetaType.UNKNOWN)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.build(ID_ANY);
		PRIMITIVE_ARRAY = MetaData.newBuilder()
				.setClassName("java.lang.Object")
				.setDomain(JAVA_DOMAIN)
				.setMetaType(MetaType.COLLECTION)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.build(ID_PRIMITIVE_ARRAY);
		OBJECT_ARRAY = MetaData.newBuilder()
				.setClassName("java.lang.Object")
				.setDomain(JAVA_DOMAIN)
				.setMetaType(MetaType.COLLECTION)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.build(ID_OBJECT_ARRAY);
		MULTI_DIMENSIONAL_ARRAY = MetaData.newBuilder()
				.setClassName("java.lang.Object")
				.setDomain(JAVA_DOMAIN)
				.setMetaType(MetaType.COLLECTION)
				.setVersion(SIMPLE_ENTITY_VERSION)
				.addEntry(MULTI_DIMENSIONAL_ARRAY_DIMENSIONS, new Type(ID_PRIMITIVE_VARINT, false))
				.addEntry(MULTI_DIMENSIONAL_ARRAY_PRIMITIVE, new Type(ID_PRIMITIVE_BOOLEAN, false))
				.addEntry(MULTI_DIMENSIONAL_ARRAY_LEAF_META_DATA, new Type(ID_ENTITY_ID, false))
				.build(ID_MULTI_DIMENSIONAL_ARRAY);
	}

}
