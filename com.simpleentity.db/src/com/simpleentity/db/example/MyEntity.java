package com.simpleentity.db.example;

import com.simpleentity.entity.Entity;
import com.simpleentity.entity.EntityBuilder;
import com.simpleentity.entity.id.EntityId;

public class MyEntity extends Entity<MyEntity> {

	private final String name;

	private MyEntity(EntityId id, Builder builder) {
		super(id);
		this.name = builder.name;
	}

	public String getName() {
		return name;
	}

	@Override
	public Builder toBuilder() {
		return new Builder(this);
	}

	public static class Builder extends EntityBuilder<MyEntity> {
		private String name = "";

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		private Builder() {
			super(null);
		}

		private Builder(MyEntity myEntity) {
			super(myEntity);
		}

		@Override
		protected MyEntity build(EntityId id) {
			return new MyEntity(id, this);
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

//	public static FluentBuilder<MyEntity> newBuilder() {
//		return new FluentBuilder<MyEntity>() {
//			@Override
//			public EntityBuilder<MyEntity> withTransaction(Transaction transaction) {
//				return new Builder();
//			}
//		};
//	}

}
