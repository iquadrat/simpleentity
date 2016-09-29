package com.simpleentity.serialize2.generic;

import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.annotation.ValueObject;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.Primitive;

@ValueObject
public abstract class GenericValue {

	public interface ValueVisitor {
		void visit(PrimitiveValue primitive);
		void visit(ValueObjectValue valueObject);
		void visit(EntityIdValue entityReference);
		void visit(CollectionValue collection);
	}

	// TODO probably add full MetaData instead just its EntityId
	// TODO remove from base class?
	public abstract EntityId getActualMetaDataId();

	public abstract void accept(ValueVisitor visitor);

	// TODO change to one class per primitive?
	public static class PrimitiveValue extends GenericValue {
		private final Primitive type;
		private final Object value;

		public PrimitiveValue(Primitive type, Object value) {
			this.type = type;
			this.value = value;
		}

		public Primitive getType() {
			return type;
		}

		public Object getValue() {
			return value;
		}

		@Override
		public EntityId getActualMetaDataId() {
			return type.getMetaDataId();
		}

		@Override
		public void accept(ValueVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public int hashCode() {
			return type.hashCode() + 31 * value.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			PrimitiveValue other = (PrimitiveValue) obj;
			return (type == other.type) && value.equals(other.value);
		}

		@Override
		public String toString() {
			return "PrimitiveValue [type=" + type + ", value=" + value + "]";
		}
	}

	public static class ValueObjectValue extends GenericValue {
		private final ObjectInfo value;

		public ValueObjectValue(ObjectInfo value) {
			this.value = value;
		}

		@Override
		public EntityId getActualMetaDataId() {
			return value.getMetaTypeId();
		}

		public ObjectInfo getValue() {
			return value;
		}

		@Override
		public void accept(ValueVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || (getClass() != obj.getClass())) {
				return false;
			}
			ValueObjectValue other = (ValueObjectValue) obj;
			return value.equals(other.value);
		}

		@Override
		public String toString() {
			return "ValueObjectValue [value=" + value + "]";
		}
	}

	public static class EntityIdValue extends GenericValue {
		private final EntityId entityId;

		public EntityIdValue(EntityId entityId) {
			this.entityId = entityId;
		}

		public EntityId getEntityId() {
			return entityId;
		}

		@Override
		public EntityId getActualMetaDataId() {
			return BootStrap.ID_ENTITY_ID;
		}

		@Override
		public void accept(ValueVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public int hashCode() {
			return 31 * entityId.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || (getClass() != obj.getClass())) {
				return false;
			}
			EntityIdValue other = (EntityIdValue) obj;
			return entityId.equals(other.entityId);
		}

		@Override
		public String toString() {
			return "EntityIdValue [entityId=" + entityId + "]";
		}
	}

	public static class CollectionValue extends GenericValue {
		private final ObjectInfo collectionInfo;
		private final ImmutableList<GenericValue> values;

		public CollectionValue(ObjectInfo collectionInfo, ImmutableList<GenericValue> values) {
			this.collectionInfo = collectionInfo;
			this.values = values;
		}

		@Override
		public EntityId getActualMetaDataId() {
			return collectionInfo.getMetaTypeId();
		}

		public ObjectInfo getCollectionInfo() {
			return collectionInfo;
		}

		public ImmutableList<GenericValue> getValues() {
			return values;
		}

		public long count() {
			return values.size();
		}

		@Override
		public void accept(ValueVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public int hashCode() {
			return collectionInfo.hashCode() + 31 * values.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || (getClass() != obj.getClass())) {
				return false;
			}
			CollectionValue other = (CollectionValue) obj;
			return collectionInfo.equals(other.collectionInfo) && values.equals(other.values);
		}

		@Override
		public String toString() {
			return "CollectionValue [collectionInfo=" + collectionInfo
					+ ", values=" + values + "]";
		}
	}

}
