package com.simpleentity.serialize2.meta;

public enum MetaType {

	PRIMITIVE(false),
	ENUM(false),
//	VALUE(false), // TODO consider adding non-polymorphic value
	VALUE_OBJECT(true),
	ENTITY(true),
	COLLECTION(true),
	// TODO add Maps
	// TODO consider adding AGGREGATION_ROOT
	UNKNOWN(true),
	;

	private final boolean polymorphic;

	private MetaType(boolean polymorphic) {
		this.polymorphic = polymorphic;
	}


	public boolean isPolymorphic() {
		return this.polymorphic;
	}


}
