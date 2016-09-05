package com.simpleentity.util.bytes;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.zip.CRC32;

import org.povworld.collection.common.ObjectUtil;

import com.simpleentity.util.StringUtil;

public class ByteChunk {
	
	public static final ByteChunk EMPTY = new ByteChunk(new byte[0], 0);

	final byte[] bytes;
	final int length;

	ByteChunk(byte[] bytes, int length) {
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
	
	public void appendTo(CRC32 crc) {
		crc.update(bytes, 0, length);
	}
	
	public void appendTo(MessageDigest digest) {
		digest.update(bytes, 0, length);
	}
	
	public ByteChunk digest(MessageDigest digest) {
		appendTo(digest);
		byte[] result = digest.digest();
		return new ByteChunk(result, result.length);
	}
	
	public ByteChunk trim(int newLength) {
		// TODO if newLenght << length, create a copy of the array
		return new ByteChunk(bytes, newLength);
	}
	
	public ByteChunk copyOfRange(int start, int length) {
		// TODO optimize
		return new ByteWriter(length).put(bytes, start, length).build();
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
		return toHexString(this);
	}
	
	public static ByteChunk copyOf(ByteBuffer buffer) {
		return new ByteWriter(buffer.remaining()).put(buffer).build();
	}
	
	public static ByteChunk copyOf(byte[] bytes) {
		return new ByteChunk(bytes.clone(), bytes.length);
	}
	
	public static ByteChunk copyOf(byte[] bytes, int offset, int length) {
		// TODO optimize
		return new ByteWriter(length).put(bytes, offset, length).build();
	}

	public static ByteBuffer toByteBuffer(ByteChunk byteChunk) {
		return ByteBuffer.wrap(byteChunk.bytes, 0, byteChunk.length).asReadOnlyBuffer();
	}
	
	public static ByteChunk fromHexString(String hex) {
		byte[] bytes = StringUtil.hex2Bytes(hex);
		return new ByteChunk(bytes, bytes.length);
	}
	
	public static String toHexString(ByteChunk byteChunk) {
		return StringUtil.bytesToHex(byteChunk.bytes, 0, byteChunk.length);
	}

	public static byte[] toByteArray(ByteChunk bytes) {
		byte[] result = new byte[bytes.length];
		System.arraycopy(bytes.bytes, 0, result, 0, bytes.length);
		return result;
	}

}
