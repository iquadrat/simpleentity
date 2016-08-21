package com.simpleentity.db.session;

import java.util.Iterator;

import org.povworld.collection.common.PreConditions;
import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.db.session.ChangeList.EntityChange;
import com.simpleentity.entity.Entity;

public class ChangeList implements Iterable<EntityChange<?>> {

	public abstract static class EntityChange<T extends Entity<T>> {
		public abstract <R> R accept(Visitor<T, R> visitor);

		public abstract Class<T> getType();
	}

	public static class Creation<T extends Entity<T>> extends EntityChange<T> {
		private final T newEntity;

		public Creation(T newEntity) {
			this.newEntity = newEntity;
		}

		@Override
		public Class<T> getType() {
			return newEntity.getType();

		}

		public T getNewEntity() {
			return newEntity;
		}

		@Override
		public <R> R accept(Visitor<T, R> visitor) {
			return visitor.visit(this);
		}
	}

	public static class Modification<T extends Entity<T>> extends EntityChange<T> {
		private final T oldEntity;
		private final T newEntity;

		public Modification(T oldEntity, T newEntity) {
			PreConditions.conditionCheck("Entity type missmatch", oldEntity.getType() == newEntity.getType());
			this.oldEntity = oldEntity;
			this.newEntity = newEntity;
		}

		@Override
		public Class<T> getType() {
			return newEntity.getType();
		}

		public T getNewEntity() {
			return newEntity;
		}

		public T getOldEntity() {
			return oldEntity;
		}

		@Override
		public <R> R accept(Visitor<T, R> visitor) {
			return visitor.visit(this);
		}

	}

	public static class Deletion<T extends Entity<T>> extends EntityChange<T> {
		private final T oldEntity;

		public Deletion(T oldEntity) {
			this.oldEntity = oldEntity;
		}

		@Override
		public Class<T> getType() {
			return oldEntity.getType();
		}

		public T getOldEntity() {
			return oldEntity;
		}

		@Override
		public <R> R accept(Visitor<T, R> visitor) {
			return visitor.visit(this);
		}

	}

	public interface Visitor<T extends Entity<T>, R> {
		public R visit(Creation<? extends T> creation);

		public R visit(Modification<? extends T> modification);

		public R visit(Deletion<? extends T> deletion);
	}

	private final ImmutableList<EntityChange<?>> changes;

	// TODO maybe create builder that collapses changes if possible
	public ChangeList(ImmutableList<EntityChange<?>> changes) {
		this.changes = changes;
	}

	@Override
	public Iterator<EntityChange<?>> iterator() {
		return changes.iterator();
	}

	public int size() {
		return changes.size();
	}
}
