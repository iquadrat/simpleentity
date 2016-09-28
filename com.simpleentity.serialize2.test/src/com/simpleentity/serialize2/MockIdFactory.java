package com.simpleentity.serialize2;

import java.util.concurrent.atomic.AtomicLong;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.id.EntityIdFactory;

public class MockIdFactory implements EntityIdFactory {

	public AtomicLong nextId = new AtomicLong(9000);

	@Override
	public EntityId newEntityId() {
		return new EntityId(nextId.getAndIncrement());
	}

}
