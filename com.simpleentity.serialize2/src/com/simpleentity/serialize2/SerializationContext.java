package com.simpleentity.serialize2;

import com.simpleentity.serialize2.meta.MetaDataRepository;




public class SerializationContext {

	private final ClassLoader classLoader;
	private final MetaDataRepository metaDataRepository;
	private final SerializerRepository serializerRepository;
	private final Instantiator instantiator;

	public SerializationContext(ClassLoader classLoader, MetaDataRepository metaDataRepository, SerializerRepository serializerRepository, Instantiator instantiator) {
		this.classLoader = classLoader;
		this.metaDataRepository = metaDataRepository;
		this.serializerRepository = serializerRepository;
		this.instantiator = instantiator;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public MetaDataRepository getMetaDataRepository() {
		return metaDataRepository;
	}

	public SerializerRepository getSerializerRepository() {
		return serializerRepository;
	}

	public Instantiator getInstantiator() {
		return instantiator;
	}

}
