package com.simpleentity.util.bytes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.simpleentity.annotation.Positive;
import com.simpleentity.util.StringUtil;

public class ByteWriter {

	public static final int DEFAULT_INITIAL_BUILDER_CAPACITY = 20;

	// TODO consider storing bytes wrapped in ByteBuffer
	private byte[] bytes;
	private int size = 0;
	private ByteOrder byteOrder;

	public ByteWriter() {
		this(DEFAULT_INITIAL_BUILDER_CAPACITY);
	}

	public ByteWriter(int initialCapacity) {
		this(initialCapacity, ByteOrder.LITTLE_ENDIAN);
	}

	public ByteWriter(int initialCapacity, ByteOrder byteOrder) {
		this.bytes = new byte[initialCapacity];
		this.byteOrder = byteOrder;
	}

	public ByteWriter setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
		return this;
	}

	public ByteWriter put(byte[] bytes) {
		ensureCapacity(size + bytes.length);
		System.arraycopy(bytes, 0, this.bytes, size, bytes.length);
		size += bytes.length;
		return this;
	}

	public ByteWriter put(byte[] bytes, int offset, int length) {
		ensureCapacity(size + length);
		System.arraycopy(bytes, offset, this.bytes, size, length);
		size += length;
		return this;
	}

	public ByteWriter put(ByteBuffer buffer) {
		int length = buffer.remaining();
		ensureCapacity(size + length);
		buffer.get(this.bytes, size, length);
		size += length;
		return this;
	}

	public ByteWriter put(ByteChunk chunk) {
		put(chunk.bytes, 0, chunk.length);
		return this;
	}

	public ByteWriter put(ByteChunk chunk, int offset, int length) {
		if (length > chunk.length) {
			throw new IndexOutOfBoundsException("length > chunk.getLength()");
		}
		put(chunk.bytes, offset, length);
		return this;
	}

	public ByteWriter putBoolean(boolean b) {
		putByte(b ? 1 : 0);
		return this;
	}

	public ByteWriter putByte(byte n) {
		ensureCapacity(size + 1);
		bytes[size] = n;
		size++;
		return this;
	}

	public ByteWriter putByte(int n) {
		return putByte((byte) n);
	}

	public ByteWriter putBytes(byte value, int count) {
		ensureCapacity(size + count);
		for (int i = size; i < size + count; ++i) {
			bytes[i] = value;
		}
		size += count;
		return this;
	}

	public ByteWriter putBytes(int value, int count) {
		return putBytes((byte) value, count);
	}

	public ByteWriter putChar(char c) {
		ensureCapacity(size + 2);
		ByteBuffer buffer = ByteBuffer.wrap(bytes, size, 2);
		buffer.order(byteOrder);
		buffer.putChar(c);
		size += 2;
		return this;
	}

	public ByteWriter putShort(short s) {
		ensureCapacity(size + 2);
		ByteBuffer buffer = ByteBuffer.wrap(bytes, size, 2);
		buffer.order(byteOrder);
		buffer.putShort(s);
		size += 2;
		return this;
	}

	public ByteWriter putInt(int n) {
		ensureCapacity(size + 4);
		ByteBuffer buffer = ByteBuffer.wrap(bytes, size, 4);
		buffer.order(byteOrder);
		buffer.putInt(n);
		size += 4;
		return this;
	}

	public ByteWriter putLong(long n) {
		ensureCapacity(size + 8);
		ByteBuffer buffer = ByteBuffer.wrap(bytes, size, 8);
		buffer.order(byteOrder);
		buffer.putLong(n);
		size += 8;
		return this;
	}

	public ByteWriter putLong(int offset, long value) {
		if (offset + 8 > size) {
			throw new IndexOutOfBoundsException((offset + 8) + " > " + size);
		}
		ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, 8);
		buffer.order(byteOrder);
		buffer.putLong(value);
		return this;
	}

	public ByteWriter putPositiveVarInt(@Positive long n) {
		// TODO consider changing to be closer to LITTLE_ENDIAN format.
		if (n < 0) {
			throw new IllegalArgumentException("Argument must be positive but was: " + n);
		}
		encodeVarInt(n);
		return this;
	}

	private void encodeVarInt(long n) {
		int bytes = (63 - Long.numberOfLeadingZeros(n)) / 7;
		// Write first data byte.
		int mask = 1 << (7 - (bytes & 0x07));
		if (bytes >= 8) {
			putByte(0);
			bytes--;
		}
		if (bytes >= 8) {
			putByte(0x40);
		} else {
			putByte((byte) (mask | (n >>> (bytes * 8))));
		}
		// Write remaining data bytes.
		for (int i = bytes - 1; i >= 0; i--) {
			putByte((byte) (n >> (i * 8)));
		}
	}

	public ByteWriter putSignedVarInt(long n) {
		if (n >= 0) {
			encodeVarInt(n << 1);
		} else {
			encodeVarInt((-(n+1) << 1)+1);
		}
		return this;
	}

	public ByteWriter putFloat(float f) {
		ensureCapacity(size + 4);
		ByteBuffer buffer = ByteBuffer.wrap(bytes, size, 4);
		buffer.order(byteOrder);
		buffer.putFloat(f);
		size += 4;
		return this;
	}

	public ByteWriter putDouble(double d) {
		ensureCapacity(size + 8);
		ByteBuffer buffer = ByteBuffer.wrap(bytes, size, 8);
		buffer.order(byteOrder);
		buffer.putDouble(d);
		size += 8;
		return this;
	}

	public ByteWriter putStringUtf8(String string) {
		byte[] encoded = string.getBytes(StringUtil.UTF8);
		putPositiveVarInt(encoded.length);
		put(encoded);
		return this;
	}

	public byte getByte(int index) {
		if (index > size) {
			throw new IndexOutOfBoundsException(index + " > " + size);
		}
		return bytes[index];
	}

	public void setByte(int index, byte value) {
		if (index > size) {
			throw new IndexOutOfBoundsException(index + " > " + size);
		}
		bytes[index] = value;
	}

	public void setByte(int index, int value) {
		setByte(index, (byte) value);
	}

	// TODO consider adding a compact() method
	public ByteChunk build() {
		// TODO return constant if size is 0
		// TODO consider shrinking if array length is >> size
		return new ByteChunk(bytes, size);
	}

	private void ensureCapacity(int capacity) {
		if (bytes.length < capacity) {
			int newCapacity = Math.max(capacity, size + size / 2);
			byte[] newBytes = new byte[newCapacity];
			System.arraycopy(bytes, 0, newBytes, 0, size);
			bytes = newBytes;
		}
	}

}