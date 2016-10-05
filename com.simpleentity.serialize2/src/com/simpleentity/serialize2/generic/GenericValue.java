package com.simpleentity.serialize2.generic;

import org.povworld.collection.immutable.ImmutableList;

import com.simpleentity.annotation.ValueObject;
import com.simpleentity.entity.id.EntityId;
import com.simpleentity.serialize2.meta.BootStrap;
import com.simpleentity.serialize2.meta.Primitive;

@ValueObject
public abstract class GenericValue extends com.simpleentity.entity.value.ValueObject {

	public interface ValueVisitor {
		void visit(NullValue nullValue);
		void visit(PrimitiveValue primitive);
		void visit(EnumValue enumValue);
		void visit(ValueObjectValue valueObject);
		void visit(EntityIdValue entityReference);
		void visit(CollectionValue collection);
	}

	// TODO probably add full MetaData instead just its EntityId
	// TODO remove from base class?
	public abstract EntityId getActualMetaDataId();

	public abstract void accept(ValueVisitor visitor);

	public static class NullValue extends GenericValue {

		private NullValue() {}

		@Override
		public EntityId getActualMetaDataId() {
			return BootStrap.ID_NULL_REFERENCE;
		}

		@Override
		public void accept(ValueVisitor visitor) {
			visitor.visit(this);
		}
	}

	private static final NullValue NULL_VALUE = new NullValue();

	public static NullValue nullValue() {
		return NULL_VALUE;
	}

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

	public static PrimitiveValue booleanValue(Boolean value) {
		return new PrimitiveValue(Primitive.BOOLEAN, value);
	}

	public static PrimitiveValue byteValue(Byte value) {
		return new PrimitiveValue(Primitive.BYTE, value);
	}

	public static PrimitiveValue charValue(Character value) {
		return new PrimitiveValue(Primitive.CHAR, value);
	}

	public static PrimitiveValue doubleValue(Double value) {
		return new PrimitiveValue(Primitive.DOUBLE, value);
	}

	public static PrimitiveValue floatValue(Float value) {
		return new PrimitiveValue(Primitive.FLOAT, value);
	}

	public static PrimitiveValue intValue(Integer value) {
		return new PrimitiveValue(Primitive.INT, value);
	}

	public static PrimitiveValue longValue(Long value) {
		return new PrimitiveValue(Primitive.LONG, value);
	}

	public static PrimitiveValue varIntValue(long value) {
		return new PrimitiveValue(Primitive.VARINT, value);
	}

	public static PrimitiveValue shortValue(Short value) {
		return new PrimitiveValue(Primitive.SHORT, value);
	}

	public static PrimitiveValue stringValue(String value) {
		return new PrimitiveValue(Primitive.STRING, value);
	}

	public static class EnumValue extends GenericValue {
		private final EntityId metaDataId;
		private final int ordinal;

		public EnumValue(EntityId metaDataId, int ordinal) {
			this.metaDataId = metaDataId;
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}

		@Override
		public EntityId getActualMetaDataId() {
			return metaDataId;
		}

		@Override
		public void accept(ValueVisitor visitor) {
			visitor.visit(this);
		}
	}

	public static <E extends Enum<E>> GenericValue enumValue(EntityId metaDataId, E value) {
		return new EnumValue(metaDataId, value.ordinal());
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
		private final EntityId elementMetaDataId;
		private final ImmutableList<GenericValue> values;

		public CollectionValue(ObjectInfo collectionInfo, EntityId elementMetaDataId, ImmutableList<GenericValue> values) {
			this.collectionInfo = collectionInfo;
			this.elementMetaDataId = elementMetaDataId;
			this.values = values;
		}

		@Override
		public EntityId getActualMetaDataId() {
			return collectionInfo.getMetaTypeId();
		}

		public ObjectInfo getCollectionInfo() {
			return collectionInfo;
		}

		public EntityId getValueMetaDataId() {
			return elementMetaDataId;
		}

		public ImmutableList<GenericValue> getValues() {
			return values;
		}

		public int getCount() {
			return values.size();
		}

		@Override
		public void accept(ValueVisitor visitor) {
			visitor.visit(this);
		}
	}

}
