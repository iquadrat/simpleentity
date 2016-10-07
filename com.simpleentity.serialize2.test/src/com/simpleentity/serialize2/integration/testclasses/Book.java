package com.simpleentity.serialize2.integration.testclasses;

import com.simpleentity.annotation.Positive;
import com.simpleentity.entity.EntityBuilder;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.util.BuilderUtil;

public class Book extends Asset<Book> {

	private final @Positive long numberOfPages;

	protected Book(EntityId id, Builder builder) {
		super(id, builder);
		this.numberOfPages = BuilderUtil.positiveLong("numberOfPages", builder.numberOfPages);
	}

	@Override
	public EntityBuilder<Book> toBuilder() {
		return new Builder(this);
	}

	public static Builder newBuilder() {
		return new Builder(null);
	}

	public static class Builder extends AbstractBuilder<Book> {

		private long numberOfPages = -1;

		protected Builder(Book book) {
			super(book);
			if (book != null) {
				this.numberOfPages = book.numberOfPages;
			}
		}

		public Builder setNumberOfPages(long numberOfPages) {
			this.numberOfPages = numberOfPages;
			return this;
		}

		@Override
		protected Book build(EntityId id) {
			return new Book(id, this);
		}

	}

}
