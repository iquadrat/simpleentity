package com.simpleentity.serialize2.meta;

import org.povworld.collection.common.PreConditions;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.annotation.Positive;
import com.simpleentity.entity.Entity;
import com.simpleentity.entity.EntityBuilder;
import com.simpleentity.entity.id.EntityId;

// TODO convert back to enum?
public class Cardinality extends Entity<Cardinality> {

	@Positive
	private final long min;
	@Positive
	private final long max;

	public Cardinality(EntityId id, long min, long max) {
		super(id);
		this.min = PreConditions.paramPositive(min);
		this.max = PreConditions.paramPositive(max);
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	@Override
	public Builder toBuilder() {
		return new Builder(this).setMin(min).setMax(max);
	}

	public static Builder newBuilder() {
		return new Builder(null);
	}

	public static class Builder extends EntityBuilder<Cardinality> {
		private long min = -1;
		private long max = -1;

		private Builder(@CheckForNull Cardinality entity) {
			super(entity);
		}

		public Builder setMin(long min) {
			this.min = min;
			return this;
		}

		public Builder setMax(long max) {
			this.max = max;
			return this;
		}

		@Override
		protected Cardinality build(EntityId id) {
			return new Cardinality(id, min, max);
		}

	}

}
