package com.simpleentity.serialize2;

public interface Instantiator {

	public <T> T newInstance(Class<T> class_);
}
