package com.simpleentity.util.io;

/*
 * Created on 16.07.2007
 */

import java.nio.*;
import java.util.Arrays;

public final class ByteBufferWriter {

	protected static final int BOOLEAN_SIZE = 1;
	protected static final int BYTE_SIZE = 1;
	protected static final int CHAR_SIZE = 2;
	protected static final int SHORT_SIZE = 2;
	protected static final int INT_SIZE = 4;
	protected static final int LONG_SIZE = 8;
	protected static final int FLOAT_SIZE = 4;
	protected static final int DOUBLE_SIZE = 8;

	protected byte[] fBytes;
	protected ByteBuffer fByteBuffer;

	/**
	 * Creates an empty buffer.
	 */
	public ByteBufferWriter() {
		this(32);
	}

	public ByteBufferWriter(int initialSize) {
		fBytes = new byte[initialSize];
		setUpByteBuffer();
	}

	private void setUpByteBuffer() {
		fByteBuffer = ByteBuffer.wrap(fBytes);
		fByteBuffer.order(ByteOrder.BIG_ENDIAN);
	}

	private void prepareWrite(int byteCount) {
		if (fByteBuffer.remaining() >= byteCount) {
			return;
		}

		int newSize = fBytes.length * 2;
		while (newSize < (fBytes.length + byteCount)) {
			newSize *= 2;
		}

		byte[] oldBytes = fBytes;
		int full = fByteBuffer.position();

		fBytes = new byte[newSize];
		setUpByteBuffer();

		fByteBuffer.put(oldBytes, 0, full);
	}

	public void putBoolean(boolean value) {
		putByte(value ? (byte) 1 : (byte) 0);
	}

	public void putByte(byte signedByte) {
		prepareWrite(BYTE_SIZE);
		fByteBuffer.put(signedByte);
	}

	public void putByteUnsigned(int unsignedByte) {
		putByte((byte) unsignedByte);
	}

	public void putChar(char c) {
		prepareWrite(CHAR_SIZE);
		fByteBuffer.putChar(c);
	}

	public void putShort(short signedShort) {
		prepareWrite(SHORT_SIZE);
		fByteBuffer.putShort(signedShort);
	}

	public void putShortUnsigned(int unsignedShort) {
		putShort((short) unsignedShort);
	}

	public void putInt(int signedInt) {
		prepareWrite(INT_SIZE);
		fByteBuffer.putInt(signedInt);
	}

	public void putIntUnsigned(long unsignedInt) {
		putInt((int) unsignedInt);
	}

	/**
	 * Encodes the given positive integer to bytes and writes it to the given
	 * writer. Writes 1 to 5 bytes depending on the value.
	 */
	public void putCompactIntUnsigned(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("Cannot write negative values: " + value);
		}

		int remaining = value;
		int next = (remaining >>> 7);
		while (next != 0) {
			putByte((byte) (((byte) remaining) | 0x80));
			remaining = next;
			next >>>= 7;
		}
		putByte((byte) remaining);
	}

	public void putLong(long signedLong) {
		prepareWrite(LONG_SIZE);
		fByteBuffer.putLong(signedLong);
	}

	public void putFloat(float f) {
		prepareWrite(FLOAT_SIZE);
		fByteBuffer.putFloat(f);
	}

	public void putDouble(double d) {
		prepareWrite(DOUBLE_SIZE);
		fByteBuffer.putDouble(d);
	}

	public void putBooleanArray(boolean[] booleans) {
		prepareWrite(INT_SIZE + booleans.length * BOOLEAN_SIZE);
		fByteBuffer.putInt(booleans.length);
		for (int i = 0; i < booleans.length; ++i) {
			fByteBuffer.put(booleans[i] ? (byte) 1 : (byte) 0);
		}
	}

	public void putByteArray(byte[] bytes) {
		prepareWrite(INT_SIZE + bytes.length * BYTE_SIZE);
		fByteBuffer.putInt(bytes.length);
		fByteBuffer.put(bytes);
	}

	public void putByteUnsignedArray(int[] bytes) {
		prepareWrite(INT_SIZE + bytes.length * BYTE_SIZE);
		fByteBuffer.putInt(bytes.length);
		for (int i = 0; i < bytes.length; ++i) {
			fByteBuffer.put((byte) bytes[i]);
		}
	}

	public void putShortArray(short[] shorts) {
		prepareWrite(INT_SIZE + shorts.length * SHORT_SIZE);
		fByteBuffer.putInt(shorts.length);
		for (int i = 0; i < shorts.length; ++i) {
			fByteBuffer.putShort(shorts[i]);
		}
	}

	public void putShortUnsignedArray(int[] shorts) {
		prepareWrite(INT_SIZE + shorts.length * SHORT_SIZE);
		fByteBuffer.putInt(shorts.length);
		for (int i = 0; i < shorts.length; ++i) {
			fByteBuffer.putShort((short) shorts[i]);
		}
	}

	public void putCharArray(char[] chars) {
		prepareWrite(INT_SIZE + chars.length * CHAR_SIZE);
		fByteBuffer.putInt(chars.length);
		for (int i = 0; i < chars.length; ++i) {
			fByteBuffer.putChar(chars[i]);
		}
	}

	public void putIntArray(int[] ints) {
		prepareWrite(INT_SIZE + ints.length * INT_SIZE);
		fByteBuffer.putInt(ints.length);
		for (int i = 0; i < ints.length; ++i) {
			fByteBuffer.putInt(ints[i]);
		}
	}

	public void putIntUnsignedArray(long[] ints) {
		prepareWrite(INT_SIZE + ints.length * INT_SIZE);
		fByteBuffer.putInt(ints.length);
		for (int i = 0; i < ints.length; ++i) {
			fByteBuffer.putInt((int) ints[i]);
		}
	}

	public void putFloatArray(float[] floats) {
		prepareWrite(INT_SIZE + floats.length * FLOAT_SIZE);
		fByteBuffer.putInt(floats.length);
		for (int i = 0; i < floats.length; ++i) {
			fByteBuffer.putFloat(floats[i]);
		}
	}

	public void putDoubleArray(double[] doubles) {
		prepareWrite(INT_SIZE + doubles.length * DOUBLE_SIZE);
		fByteBuffer.putInt(doubles.length);
		for (int i = 0; i < doubles.length; ++i) {
			fByteBuffer.putDouble(doubles[i]);
		}
	}

	public ByteBuffer getByteBuffer() {
		return fByteBuffer;
	}

	/**
	 * @return the number of filled bytes in the buffer
	 */
	public int position() {
		return fByteBuffer.position();
	}

	public void clear() {
		fByteBuffer.clear();
	}

	@Override
	public String toString() {
		int pos = position();
		byte[] copy = new byte[pos];
		System.arraycopy(fBytes, 0, copy, 0, pos);
		return Arrays.toString(copy);
	}

	public void write(int b) {
		putByteUnsigned(b);
	}

	public void write(byte b[]) {
		write(b, 0, b.length);
	}

	public void write(byte b[], int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		}
		prepareWrite(len * BYTE_SIZE);
		fByteBuffer.put(b, off, len);
	}

}
