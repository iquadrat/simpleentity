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
			return type.getEntityId();
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

	// public static class ByteValue extends FieldValue {
	// private final byte value;
	// public ByteValue(byte value) {
	// this.value = value;
	// }
	// }
	//
	// public static class CharValue extends FieldValue {
	// private final char value;
	// public CharValue(char value) {
	// this.value = value;
	// }
	// }
	//
	// public static class ShortValue extends FieldValue {
	// private final short value;
	// public ShortValue(short value) {
	// this.value = value;
	// }
	// }
	//
	// public static class IntValue extends FieldValue {
	// private final int value;
	// public IntValue(int value) {
	// this.value = value;
	// }
	// }

	// TODO add more primitives

	public static class ValueObjectValue extends GenericValue {
		private final EntityId actualType;
		private final ObjectInfo value;

		public ValueObjectValue(EntityId type, ObjectInfo value) {
			this.actualType = type;
			this.value = value;
		}

		@Override
		public EntityId getActualMetaDataId() {
			return actualType;
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
			return actualType.hashCode() + 31 * value.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || (getClass() != obj.getClass())) {
				return false;
			}
			ValueObjectValue other = (ValueObjectValue) obj;
			return actualType.equals(other.actualType) && value.equals(other.value);
		}

		@Override
		public String toString() {
			return "ValueObjectValue [actualType=" + actualType + ", value=" + value + "]";
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
			return BootStrap.ID_ENTITIY_ID;
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
		private final EntityId actualMetaDataId;
		private final ObjectInfo collectionInfo;
		private final ImmutableList<GenericValue> values;

		public CollectionValue(EntityId actualMetaDataId, ObjectInfo collectionInfo, ImmutableList<GenericValue> values) {
			this.actualMetaDataId = actualMetaDataId;
			this.collectionInfo = collectionInfo;
			this.values = values;
		}

		@Override
		public EntityId getActualMetaDataId() {
			return actualMetaDataId;
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
			return actualMetaDataId.hashCode() + 31 * collectionInfo.hashCode() + 31 * 31 * values.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || (getClass() != obj.getClass())) {
				return false;
			}
			CollectionValue other = (CollectionValue) obj;
			return actualMetaDataId.equals(other.actualMetaDataId) && collectionInfo.equals(other.collectionInfo)
					&& values.equals(other.values);
		}

		@Override
		public String toString() {
			return "CollectionValue [actualMetaDataId=" + actualMetaDataId + ", collectionInfo=" + collectionInfo
					+ ", values=" + values + "]";
		}
	}

}
