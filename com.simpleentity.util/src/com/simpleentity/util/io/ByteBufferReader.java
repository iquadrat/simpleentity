package com.simpleentity.util.io;

import java.nio.*;

import com.simpleentity.util.Assert;

public final class ByteBufferReader {

	private final ByteBuffer fBuffer;

	public ByteBufferReader(ByteBuffer buffer) {
		fBuffer = buffer;
	}

	public boolean hasRemaining() {
		return fBuffer.hasRemaining();
	}

	public int remaining() {
		return fBuffer.remaining();
	}

	public boolean readBoolean() {
		switch (readByte()) {
		case 0:
			return false;
		case 1:
			return true;
		default:
			throw Assert.fail("Boolean value is neither 0 nor 1");
		}
	}

	public byte readByte() {
		return fBuffer.get();
	}

	public char readChar() {
		return fBuffer.getChar();
	}

	public double readDouble() {
		return fBuffer.getDouble();
	}

	public float readFloat() {
		return fBuffer.getFloat();
	}

	public int readInt() {
		return fBuffer.getInt();
	}

	/**
	 * Reads an integer from the given reader which has been written using
	 * {@link ByteBufferWriter#putCompactIntUnsigned(int)}.
	 */
	public int readCompactIntUnsigned() {
		int result = 0;
		int value = readUnsignedByte();
		int factor = 1;
		while (value > 127) {
			result += (value ^ 0x80) * factor;
			value = readUnsignedByte();
			factor <<= 7;
		}
		return result + value * factor;
	}

	public short readShort() {
		return fBuffer.getShort();
	}

	public long readLong() {
		return fBuffer.getLong();
	}

	public int readUnsignedByte() {
		return fBuffer.get() & 0xFF;
	}

	public long readUnsignedInt() {
		return fBuffer.getInt() & 0xFFFFFFFFL;
	}

	public int readUnsignedShort() {
		return fBuffer.getShort() & 0xFFFF;
	}

	public boolean[] readBoolArray() {
		int length = readInt();
		boolean[] result = new boolean[length];
		for (int i = 0; i < length; ++i) {
			result[i] = readBoolean();
		}
		return result;
	}

	public byte[] readByteArray() {
		int length = readInt();
		byte[] result = new byte[length];
		for (int i = 0; i < length; ++i) {
			result[i] = readByte();
		}
		return result;
	}

	public double[] readDoubleArray() {
		int length = readInt();
		double[] result = new double[length];
		for (int i = 0; i < length; ++i) {
			result[i] = readDouble();
		}
		return result;
	}

	public float[] readFloatArray() {
		int length = readInt();
		float[] result = new float[length];
		for (int i = 0; i < length; ++i) {
			result[i] = readFloat();
		}
		return result;
	}

	public int[] readIntArray() {
		int length = readInt();
		int[] result = new int[length];
		for (int i = 0; i < length; ++i) {
			result[i] = readInt();
		}
		return result;
	}

	public short[] readShortArray() {
		int length = readInt();
		short[] result = new short[length];
		for (int i = 0; i < length; ++i) {
			result[i] = readShort();
		}
		return result;
	}

	public void read(byte[] content) {
		fBuffer.get(content);
	}

	public void read(byte[] content, int off, int len) {
		fBuffer.get(content, off, len);
	}

	public void skip(int length) {
		fBuffer.position(fBuffer.position() + length);
	}

	public ByteBuffer getBuffer() {
		return fBuffer;
	}

}
