package com.simpleentity.serialize2.meta;

import com.simpleentity.entity.Entity;
import com.simpleentity.entity.EntityBuilder;
import com.simpleentity.entity.id.EntityId;

public class MetaData extends Entity<MetaData> {

	private MetaData(EntityId id, Builder builder) {
		super(MetaData.class, id);
		// TODO initialize fields
	}

	@Override
	public EntityBuilder<MetaData> toBuilder() {
		return new Builder();
	}

	public static class Builder extends EntityBuilder<MetaData> {

		@Override
		protected MetaData build(EntityId id) {
			return new MetaData(id, this);
		}

	}

}
