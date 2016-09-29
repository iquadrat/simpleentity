package com.simpleentity.serialize2.java;

import com.simpleentity.serialize2.SerializerRepository;



public class JavaSerializationContext {

	private final ClassLoader classLoader;
	private final SerializerRepository serializerRepository;
	private final Instantiator instantiator;

	public JavaSerializationContext(ClassLoader classLoader, SerializerRepository serializerRepository, Instantiator instantiator) {
		this.classLoader = classLoader;
		this.serializerRepository = serializerRepository;
		this.instantiator = instantiator;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public SerializerRepository getSerializerRepository() {
		return serializerRepository;
	}

	public Instantiator getInstantiator() {
		return instantiator;
	}

}
