package com.simpleentity.serialize2.meta;

import com.simpleentity.entity.id.EntityId;


public interface MetaDataFactory {

	public MetaData create(Class<?> class_, EntityId id, MetaDataRepository repository);

}
