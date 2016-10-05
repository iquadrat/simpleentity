package com.simpleentity.entity.id;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicIdFactory implements EntityIdFactory {

	private final AtomicLong nextId;

	public AtomicIdFactory(long firstId) {
		this.nextId = new AtomicLong(firstId);
	}

	@Override
	public EntityId newEntityId() {
		long id = nextId.getAndIncrement();
		return new EntityId(id);
	}

}
