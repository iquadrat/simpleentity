package com.simpleentity.testmodel;

import javax.annotation.CheckForNull;

import com.simpleentity.entity.Entity;
import com.simpleentity.entity.EntityBuilder;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.util.BuilderUtil;

public abstract class Asset<T extends Asset<T>> extends Entity<T> {

	private final EntityId artist;
	private final String title;
	private final Date releaseDate;

	protected Asset(EntityId id, AbstractBuilder<T> builder) {
		super(id);
		this.artist = BuilderUtil.requiredBuilderField("artist", builder.artist);
		this.title = BuilderUtil.requiredBuilderField("title", builder.title);
		this.releaseDate = BuilderUtil.requiredBuilderField("releaseDate", builder.releaseDate);
	}

	public EntityId getArtist() {
		return artist;
	}

	public String getTitle() {
		return title;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public abstract static class AbstractBuilder<T extends Asset<T>> extends EntityBuilder<T> {

		private @CheckForNull EntityId artist;
		private @CheckForNull String title;
		private @CheckForNull Date releaseDate;

		protected AbstractBuilder(@CheckForNull T asset) {
			super(asset);
			if (asset != null) {
				this.artist = asset.getArtist();
				this.title = asset.getTitle();
				this.releaseDate = asset.getReleaseDate();
			}
		}

		public AbstractBuilder<T> setArtist(EntityId artist) {
			this.artist = artist;
			return this;
		}

		public AbstractBuilder<T> setTitle(String title) {
			this.title = title;
			return this;
		}

		public AbstractBuilder<T> setReleaseDate(Date releaseDate) {
			this.releaseDate = releaseDate;
			return this;
		}
	}

}
