package com.simpleentity.serialize2.binary;

import com.simpleentity.serialize2.SerializerException;
import com.simpleentity.serialize2.meta.Primitive;
import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

public abstract class PrimitiveSerializer<T> implements BinarySerializer<T> {

	static final BinarySerializer<Boolean> BOOLEAN = new PrimitiveSerializer<Boolean>() {
		@Override
		public void serialize(Boolean value, ByteWriter destination) {
			destination.putBoolean(value);
		};

		@Override
		public Boolean deserialize(ByteReader source) {
			return source.getBoolean();
		}
	};

	static final BinarySerializer<Byte> BYTE = new PrimitiveSerializer<Byte>() {
		@Override
		public void serialize(Byte value, ByteWriter destination) {
			destination.putByte(value);
		};

		@Override
		public Byte deserialize(ByteReader source) {
			return source.getByte();
		}
	};
	static final BinarySerializer<Character> CHAR = new PrimitiveSerializer<Character>() {
		@Override
		public void serialize(Character value, ByteWriter destination) {
			destination.putChar(value);
		};

		@Override
		public Character deserialize(ByteReader source) {
			return source.getChar();
		}
	};
	static final BinarySerializer<Double> DOUBLE = new PrimitiveSerializer<Double>() {
		@Override
		public void serialize(Double value, ByteWriter destination) {
			destination.putDouble(value);
		}

		@Override
		public Double deserialize(ByteReader source) {
			return source.getDouble();
		}
	};
	static final BinarySerializer<Float> FLOAT = new PrimitiveSerializer<Float>() {

		@Override
		public void serialize(Float value, ByteWriter destination) {
			destination.putFloat(value);
		}

		@Override
		public Float deserialize(ByteReader source) {
			return source.getFloat();
		}

	};
	static final BinarySerializer<Integer> INT = new PrimitiveSerializer<Integer>() {

		@Override
		public void serialize(Integer value, ByteWriter destination) {
			destination.putInt(value);
		}

		@Override
		public Integer deserialize(ByteReader source) {
			return source.getInt();
		}
	};
	static final BinarySerializer<Long> LONG = new PrimitiveSerializer<Long>() {

		@Override
		public void serialize(Long value, ByteWriter destination) {
			destination.putLong(value);
		}

		@Override
		public Long deserialize(ByteReader source) {
			return source.getLong();
		}
	};
	static final BinarySerializer<Number> VARINT = new PrimitiveSerializer<Number>() {
		@Override
		public void serialize(Number value, ByteWriter destination) {
			long v = value.longValue();
			if (v < 0) {
				throw new SerializerException("Cannot serialize negative var int: "+v);
			}
			destination.putPositiveVarInt(v);
		}

		@Override
		public Long deserialize(ByteReader source) {
			return source.getPositiveVarInt();
		}

	};
	static final BinarySerializer<Short> SHORT = new PrimitiveSerializer<Short>() {

		@Override
		public void serialize(Short value, ByteWriter destination) {
			destination.putShort(value);
		}

		@Override
		public Short deserialize(ByteReader source) {
			return source.getShort();
		}
	};
	static final BinarySerializer<String> STRING = new PrimitiveSerializer<String>() {

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
		case BOOLEAN:
			return BOOLEAN;
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
		case VARINT:
			return VARINT;
		case SHORT:
			return SHORT;
		case STRING:
			return STRING;
		default:
			throw new IllegalStateException("Unknown primivite " + primitive);
		}

	}
}
