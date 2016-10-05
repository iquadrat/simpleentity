package com.simpleentity.serialize2.meta;

import com.simpleentity.entity.value.ValueObject;

public final class Entry extends ValueObject {

	private final String id;
	private final Type type;

	public Entry(String id, Type type) {
		this.id = id;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return id + "=" + type;
	}

}
