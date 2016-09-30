package com.simpleentity.serialize2;

import java.util.concurrent.atomic.AtomicLong;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.entity.id.EntityIdFactory;

public class MockIdFactory implements EntityIdFactory {

	public final AtomicLong nextId ;

	public MockIdFactory() {
		this(9000);
	}

	public MockIdFactory(EntityId nextId) {
		this(nextId.getId());
	}

	public MockIdFactory(long nextId) {
		this.nextId = new AtomicLong(nextId);
	}

	@Override
	public EntityId newEntityId() {
		return new EntityId(nextId.getAndIncrement());
	}

}
