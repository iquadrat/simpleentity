package com.simpleentity.serialize2.integration.testclasses;

import javax.annotation.CheckForNull;

import com.simpleentity.entity.Entity;
import com.simpleentity.entity.EntityBuilder;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.util.BuilderUtil;

public class Artist extends Entity<Artist> {

	private final String name;
	private final Sex sex;
	private final @CheckForNull Date birthDate;

	private Artist(EntityId id, Builder builder) {
		super(id);
		this.name = BuilderUtil.requiredBuilderField("name", builder.name);
		this.sex = BuilderUtil.requiredBuilderField("sex", builder.sex);
		this.birthDate = builder.birthDate;
	}

	public String getName() {
		return name;
	}

	public Sex getSex() {
		return sex;
	}

	@Override
	public Builder toBuilder() {
		return new Builder(this);
	}

	public static Builder newBuilder() {
		return new Builder(null);
	}

	public static class Builder extends EntityBuilder<Artist> {

		private @CheckForNull String name;
		private @CheckForNull Sex sex;
		private @CheckForNull Date birthDate;

		private Builder(@CheckForNull Artist entity) {
			super(entity);
			if (entity != null) {
				this.name = entity.name;
				this.sex = entity.sex;
				this.birthDate = entity.birthDate;
			}
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setSex(Sex sex) {
			this.sex = sex;
			return this;
		}

		public Builder setBirthDate(Date birthDate) {
			this.birthDate = birthDate;
			return this;
		}

		@Override
		protected Artist build(EntityId id) {
			return new Artist(id, this);
		}

	}

}
