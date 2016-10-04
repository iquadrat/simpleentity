package com.simpleentity.serialize2;


public abstract class AbstractSerializer<T> implements Serializer<T> {

	private final Class<T> type;

	public AbstractSerializer(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

}
