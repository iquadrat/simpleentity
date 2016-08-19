package com.simpleentity.db.transaction;

import com.simpleentity.db.Entity;
import com.simpleentity.db.EntityId;

public interface ReadWriteSession extends Session {
	public void create(Entity<?> entity);

	public void modify(Entity<?> entity);

	public void delete(Entity<?> entity);

	public void commit();

	public void revert();

	public EntityId newEntityId();
}
