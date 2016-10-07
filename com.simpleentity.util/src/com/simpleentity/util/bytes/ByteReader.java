package com.simpleentity.util.bytes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.povworld.collection.common.MathUtil;

import com.simpleentity.util.StringUtil;

public class ByteReader {

	// private final ByteChunk bytes;
	// private ByteOrder byteOrder;
	// private int position;
	private final ByteBuffer bytes;

	public ByteReader(ByteChunk bytes) {
		this(bytes, 0);
	}

	public ByteReader(ByteChunk bytes, int offset) {
		this.bytes = ByteBuffer.wrap(bytes.bytes, offset, bytes.length - offset);
		this.bytes.order(ByteOrder.LITTLE_ENDIAN);
		// this.position = offset;
	}

	public void setByteOrder(ByteOrder byteOrder) {
		// this.byteOrder = byteOrder;
		bytes.order(byteOrder);
	}

	public boolean getBoolean() {
		return getByte() != 0;
	}

	public byte getByte() {
		return bytes.get();
	}

	public byte get() {
		return bytes.get();
	}

	public void get(byte[] bytes) {
		this.bytes.get(bytes);
	}

	public void get(byte[] bytes, int offset, int length) {
		this.bytes.get(bytes, offset, length);
	}

	public short getShort() {
		return bytes.getShort();
	}

	public int getInt() {
		return bytes.getInt();
	}

	public long getLong() {
		return bytes.getLong();
	}

	public float getFloat() {
		return bytes.getFloat();
	}

	public double getDouble() {
		return bytes.getDouble();
	}

	public char getChar() {
		return bytes.getChar();
	}

	public String getStringUtf8() {
		byte[] encoded = new byte[MathUtil.longToInt(getPositiveVarInt())];
		get(encoded);
		return new String(encoded, StringUtil.UTF8);
	}

	/**
	 * @throws IllegalStateException
	 *             if bytes read from the current position do not decode to a
	 *             valid long value because either end of the buffer is reached
	 *             or the decoded value would be outside of the long range.
	 */
	public long getPositiveVarInt() {
		bytes.mark();
		try {
			long value = readVarInt(); // TODO check that it is positive
			if (value < 0) {
				throw new IllegalStateException("Value out of long range!");
			}
			return value;
		} catch (Exception e) {
			// Reset position:
			bytes.reset();
			throw new IllegalStateException(e);
		}
	}

	private long readVarInt() {
		byte first = getByte();
		int bytes = Integer.numberOfLeadingZeros(first & 0xff) - 24;
		long result;
		if (bytes == 8) {
			byte second = getByte();
			if ((second & 0x80) == 0 && (second != 0x40)) {
				throw new IllegalStateException("Value out of long range!");
			}
			result = second & 0x7f;
			bytes = Integer.numberOfLeadingZeros(second & 0xff) - 17;
		} else {
			result = first & (0x7f >> bytes);
		}
		for (int i = 0; i < bytes; ++i) {
			result = (result << 8) | (getByte() & 0xff);
		}
		return result;

	}

	public long getSignedVarInt() {
		long n = readVarInt();
		if ((n & 1) == 0) {
			return n >>> 1;
		}
		return -(n >>> 1) - 1;
	}

}
