package com.simpleentity.serialize2.binary;

import com.simpleentity.serialize2.BinarySerializer;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

public abstract class PrimitiveSerializer<T> implements BinarySerializer<T> {

	private final Class<T> type;

	private PrimitiveSerializer(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	static final BinarySerializer<Byte> BYTE = new PrimitiveSerializer<Byte>(Byte.class) {
		@Override
		public void serialize(Byte value, ByteWriter destination) {
			destination.putByte(value);
		};

		@Override
		public Byte deserialize(ByteReader source) {
			return source.getByte();
		}
	};
	static final BinarySerializer<Character> CHAR = new PrimitiveSerializer<Character>(Character.class) {
		@Override
		public void serialize(Character value, ByteWriter destination) {
			destination.putChar(value);
		};

		@Override
		public Character deserialize(ByteReader source) {
			return source.getChar();
		}
	};
	static final BinarySerializer<Double> DOUBLE = new PrimitiveSerializer<Double>(Double.class) {
		@Override
		public void serialize(Double value, ByteWriter destination) {
			destination.putDouble(value);
		}

		@Override
		public Double deserialize(ByteReader source) {
			return source.getDouble();
		}
	};
	static final BinarySerializer<Float> FLOAT = new PrimitiveSerializer<Float>(Float.class) {

		@Override
		public void serialize(Float value, ByteWriter destination) {
			destination.putFloat(value);
		}

		@Override
		public Float deserialize(ByteReader source) {
			return source.getFloat();
		}

	};
	static final BinarySerializer<Integer> INT = new PrimitiveSerializer<Integer>(Integer.class) {

		@Override
		public void serialize(Integer value, ByteWriter destination) {
			destination.putInt(value);
		}

		@Override
		public Integer deserialize(ByteReader source) {
			return source.getInt();
		}
	};
	static final BinarySerializer<Long> LONG = new PrimitiveSerializer<Long>(Long.class) {

		@Override
		public void serialize(Long value, ByteWriter destination) {
			destination.putLong(value);
		}

		@Override
		public Long deserialize(ByteReader source) {
			return source.getLong();
		}
	};
	static final BinarySerializer<Short> SHORT = new PrimitiveSerializer<Short>(Short.class) {

		@Override
		public void serialize(Short value, ByteWriter destination) {
			destination.putShort(value);
		}

		@Override
		public Short deserialize(ByteReader source) {
			return source.getShort();
		}
	};
	static final BinarySerializer<String> STRING = new PrimitiveSerializer<String>(String.class) {

		@Override
		public void serialize(String value, ByteWriter destination) {
			destination.putStringUtf8(value);
		}

		@Override
		public String deserialize(ByteReader source) {
			return source.getStringUtf8();
		}
	};

	public static BinarySerializer<?> getSerializer(Primitive primitive) {
		switch (primitive) {
		case BYTE:
			return BYTE;
		case CHAR:
			return CHAR;
		case DOUBLE:
			return DOUBLE;
		case FLOAT:
			return FLOAT;
		case INT:
			return INT;
		case LONG:
			return LONG;
		case SHORT:
			return SHORT;
		case STRING:
			return STRING;
		default:
			throw new IllegalStateException("Unknown primivite " + primitive);
		}

	}
}
