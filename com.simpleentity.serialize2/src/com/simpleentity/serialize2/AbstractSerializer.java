package com.simpleentity.serialize2;


public abstract class AbstractSerializer<T> implements Serializer<T> {

	private final Class<T> type;
//	private final EntityId metaDataId;

	public AbstractSerializer(Class<T> type /*, EntityId metaDataId */) {
		this.type = type;
//		this.metaDataId = metaDataId;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

//	@Override
//	public EntityId getMetaDataId() {
//		return metaDataId;
//	}

}
