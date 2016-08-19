package com.simpleentity.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.povworld.collection.common.ObjectUtil;

public class ByteChunk {

	public static final int DEFAULT_INITIAL_BUILDER_CAPACITY = 20;

	// TODO consider storing bytes wrapped in ByteBuffer
	private final byte[] bytes;
	private final int length;

	private ByteChunk(byte[] bytes, int length) {
		this.bytes = bytes;
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public byte get(int index) {
		if (index >= length) {
			throw new IndexOutOfBoundsException(index + ">=" + length);
		}
		return bytes[index];
	}

	public void get(ByteBuffer buffer) {
		buffer.put(bytes, 0, length);
	}

	@Override
	public int hashCode() {
		int result = 1;
		for (int i = 0; i < length; ++i) {
			result = 31 * result + bytes[i];
		}
		return result;
	}

	@Override
	public boolean equals(Object object) {
		ByteChunk other = ObjectUtil.castOrNull(object, ByteChunk.class);
		if ((other == null) || (other.length != this.length)) {
			return false;
		}
		for (int i = 0; i < length; ++i) {
			if (other.bytes[i] != this.bytes[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return StringUtil.bytesToHex(bytes, 0, length);
	}
	
	public ByteChunk fromByteBuffer(ByteBuffer buffer) {
		return newBuilder().append(buffer).build();
	}

	public static Builder newBuilder() {
		return newBuilder(DEFAULT_INITIAL_BUILDER_CAPACITY);
	}

	public static Builder newBuilder(int initialCapacity) {
		return new Builder(initialCapacity);
	}

	public static class Builder {
		private byte[] bytes;
		private int size = 0;
		private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

		protected Builder(int initialCapacity) {
			this.bytes = new byte[initialCapacity];
		}

		public Builder setByteOrder(ByteOrder byteOrder) {
			this.byteOrder = byteOrder;
			return this;
		}

		public Builder append(byte[] bytes) {
			ensureCapacity(size + bytes.length);
			System.arraycopy(bytes, 0, this.bytes, size, bytes.length);
			size += bytes.length;
			return this;
		}

		public Builder append(byte[] bytes, int offset, int length) {
			ensureCapacity(size + length);
			System.arraycopy(bytes, offset, this.bytes, size, length);
			size += length;
			return this;
		}

		public Builder append(ByteBuffer buffer) {
			int length = buffer.remaining();
			ensureCapacity(size + length);
			buffer.get(this.bytes, size, length);
			return this;
		}

		public Builder append(ByteChunk chunk) {
			append(chunk.bytes, 0, chunk.length);
			return this;
		}

		public Builder appendInt(int n) {
			ensureCapacity(size + 4);
			ByteBuffer buffer = ByteBuffer.wrap(bytes, size, 4);
			buffer.order(byteOrder);
			buffer.putInt(n);
			return this;
		}

		public Builder appendLong(long n) {
			ensureCapacity(size + 8);
			ByteBuffer buffer = ByteBuffer.wrap(bytes, size, 8);
			buffer.order(byteOrder);
			buffer.putLong(n);
			return this;
		}

		public ByteChunk build() {
			// TODO return constant if size is 0
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

	public ByteBuffer asByteBuffer() {
		return ByteBuffer.wrap(bytes, 0, length).asReadOnlyBuffer();
	}

}
