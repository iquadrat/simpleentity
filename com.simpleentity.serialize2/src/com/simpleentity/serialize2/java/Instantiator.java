package com.simpleentity.serialize2.java;

public interface Instantiator {

	public <T> T newInstance(Class<T> class_);
}
