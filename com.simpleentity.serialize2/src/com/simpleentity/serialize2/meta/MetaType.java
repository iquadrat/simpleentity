package com.simpleentity.serialize2.meta;

public enum MetaType {

	PRIMITIVE(false),
//	VALUE(false), // TODO consider adding non-polymorphic value
	VALUE_OBJECT(true),
	ENTITY(true),
	COLLECTION(true)
	// TODO consider adding AGGREGATION_ROOT
	;

	private final boolean polymorphic;

	private MetaType(boolean polymorphic) {
		this.polymorphic = polymorphic;
	}


	public boolean isPolymorphic() {
		return this.polymorphic;
	}


}
