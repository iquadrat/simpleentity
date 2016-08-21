package com.simpleentity.db.session;

import com.simpleentity.entity.Entity;
import com.simpleentity.entity.EntityBuilder;
import com.simpleentity.entity.id.EntityId;

public interface ReadWriteSession extends Session {
	public <T extends Entity<T>> T create(EntityBuilder<T> builder);

	public <T extends Entity<T>> T modify(EntityBuilder<T> builder);

	public void delete(Entity<?> entity);
	
	public void commit();

	public void revert();

	public EntityId newEntityId();
}
