package com.simpleentity.util.bytes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.simpleentity.util.StringUtil;

public class ByteWriter {

	public static final int DEFAULT_INITIAL_BUILDER_CAPACITY = 20;

	// TODO consider storing bytes wrapped in ByteBuffer
	private byte[] bytes;
	private int size = 0;
	private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

	public ByteWriter() {
		this(DEFAULT_INITIAL_BUILDER_CAPACITY);
	}

	public ByteWriter(int initialCapacity) {
		this.bytes = new byte[initialCapacity];
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
		return this;
	}

	public ByteWriter put(ByteChunk chunk) {
		put(chunk.bytes, 0, chunk.length);
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

	public ByteWriter putInt(int n) {
		ensureCapacity(size + 4);
		ByteBuffer buffer = ByteBuffer.wrap(bytes, size, 4);
		buffer.order(byteOrder);
		buffer.putInt(n);
		return this;
	}

	public ByteWriter putLong(long n) {
		ensureCapacity(size + 8);
		ByteBuffer buffer = ByteBuffer.wrap(bytes, size, 8);
		buffer.order(byteOrder);
		buffer.putLong(n);
		return this;
	}

	public ByteWriter putVarInt(long n) {
		if (n < 0) {
			throw new IllegalArgumentException("Argument must be positive but was: " + n);
		}
		int bytes = (63 - Long.numberOfLeadingZeros(n)) / 7;
		// Write first data byte.
		int mask = 1 << (7 - (bytes & 0x07));
		if (bytes == 8) {
			putByte(0);
			bytes = 7;
		}
		putByte((byte)(mask | (n >> (bytes * 8))));
		// Write remaining data bytes.
		for (int i = bytes - 1; i >= 0; i--) {
			putByte((byte) (n >> (i * 8)));
		}
		return this;
	}
	
	public ByteWriter putStringUtf8(String string) {
		byte[] encoded = string.getBytes(StringUtil.UTF8);
		putVarInt(encoded.length);
		put(encoded);
		return this;
	}

	// TODO consider adding a compact() method
	public ByteChunk build() {
		// TODO return constant if size is 0
		// TODO consider shrinking if array length is >> size
		return new ByteChunk(bytes, size);
	}

	private void ensureCapacity(int capacity) {
		if (size < capacity) {
			int newCapacity = Math.max(capacity, size + size / 2);
			byte[] newBytes = new byte[newCapacity];
			System.arraycopy(bytes, 0, newBytes, 0, size);
			bytes = newBytes;
		}
	}
}