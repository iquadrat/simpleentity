package com.simpleentity.util.bytes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.simpleentity.util.StringUtil;

public class ByteReader {

//	private final ByteChunk bytes;
//	private ByteOrder byteOrder;
//	private int position;
	private final ByteBuffer bytes;

	public ByteReader(ByteChunk bytes) {
		this(bytes, 0);
	}

	public ByteReader(ByteChunk bytes, int offset) {
		this.bytes = ByteBuffer.wrap(bytes.bytes, offset, bytes.length - offset);
		this.bytes.order(ByteOrder.LITTLE_ENDIAN);
//		this.position = offset;
	}
	
	public void setByteOrder(ByteOrder byteOrder) {
//		this.byteOrder = byteOrder;
		bytes.order(byteOrder);
	}
	
	public boolean getBoolean() {
		return getByte() != 0;
	}

	public byte getByte() {
		return bytes.get();
//		checkAvailable(1);
//		byte result = bytes.get(position);
//		position++;
//		return result;
	}

//	private void checkAvailable(int i) {
//		if (position + i > bytes.length) {
//			throw new IllegalStateException("Not enough data, require " + i + " bytes but position is at " + position
//					+ " of " + bytes.length);
//		}
//	}
	
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
		byte[] encoded = new byte[(int)getVarInt()];
		get(encoded);
		return new String(encoded, StringUtil.UTF8);
	}


	public long getVarInt() {
		bytes.mark();
		try {
			byte first = getByte();
			int bytes = Integer.numberOfLeadingZeros(first & 0xff) - 24;
			long result;
			if (bytes == 8) {
				result = getByte() & 0x7f;
				bytes = 7;
			} else {
				result = first & (0x7f >> bytes);
			}
			for (int i = 0; i < bytes; ++i) {
				result = (result << 8) | (getByte() & 0xff);
			}
			return result;
		} catch (Exception e) {
			// Reset position:
			bytes.reset();
			throw e;
		}
	}

	
}
