package com.simpleentity.testmodel;

import javax.annotation.CheckForNull;

import org.povworld.collection.immutable.ImmutableArrayList;
import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.entity.id.EntityId;

public class Album extends Asset<Album> {

	private final ImmutableList<Song> songs;

	private Album(EntityId id, Builder builder) {
		super(id, builder);
		this.songs = builder.songs.build();
	}

	public ImmutableList<Song> getSongs() {
		return songs;
	}

	public Duration totalLength() {
		Duration result = new Duration(0, 0);
		for(Song song: songs) {
			result = Duration.add(result, song.getDuration());
		}
		return result;
	}

	@Override
	public Builder toBuilder() {
		return new Builder(this);
	}

	public static Builder newBuilder() {
		return new Builder(null);
	}

	public static class Builder extends AbstractBuilder<Album> {
		private final ImmutableArrayList.Builder<Song> songs = ImmutableArrayList.newBuilder();

		private Builder(@CheckForNull Album entity) {
			super(entity);
			if (entity != null) {
				this.songs.addAll(entity.songs);
			}
		}

		public Builder addSong(Song song) {
			songs.add(song);
			return this;
		}

		@Override
		protected Album build(EntityId id) {
			return new Album(id, this);
		}
	}

}
