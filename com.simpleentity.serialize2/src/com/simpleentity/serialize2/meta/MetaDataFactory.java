package com.simpleentity.serialize2.meta;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.SerializerRepository;


public interface MetaDataFactory {

	public MetaData create(Class<?> class_, EntityId id, SerializerRepository serializerRepository);

}
