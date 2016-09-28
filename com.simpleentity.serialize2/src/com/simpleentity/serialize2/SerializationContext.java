package com.simpleentity.serialize2;



public class SerializationContext {

	private final ClassLoader classLoader;
	private final SerializerRepository serializerRepository;
	private final Instantiator instantiator;

	public SerializationContext(ClassLoader classLoader, SerializerRepository serializerRepository, Instantiator instantiator) {
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
