package com.simpleentity.util.bytes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link ByteReader}.
 */
public class ByteReaderTest {
	@Test
	public void getVarInt() {
		assertEquals(0, decodeVarInt("80"));
		assertEquals(42, decodeVarInt("aa"));
		assertEquals(127, decodeVarInt("ff"));
		assertEquals(128, decodeVarInt("4080"));
		assertEquals(0x1234, decodeVarInt("5234"));
		assertEquals(0x3fff, decodeVarInt("7fff"));
		assertEquals(0x4001, decodeVarInt("204001"));
		assertEquals(0x1fffff, decodeVarInt("3fffff"));
		assertEquals(0x200000, decodeVarInt("10200000"));
		assertEquals(0x0fffffff, decodeVarInt("1fffffff"));
		assertEquals(0x10000000, decodeVarInt("0810000000"));
		assertEquals(Integer.MAX_VALUE, decodeVarInt("087fffffff"));
		assertEquals(0x1234567890L, decodeVarInt("041234567890"));
		assertEquals(0x12345678901L, decodeVarInt("052345678901"));
		assertEquals(0x123456789012L, decodeVarInt("02123456789012"));
		assertEquals(0x1234567890123L, decodeVarInt("03234567890123"));
		assertEquals(0x12345678901234L, decodeVarInt("0112345678901234"));
		assertEquals(0x123456789012345L, decodeVarInt("008123456789012345"));
		assertEquals(Long.MAX_VALUE, decodeVarInt("00ffffffffffffffff"));
	}

	private long decodeVarInt(String hex) {
		ByteReader reader = new ByteReader(ByteChunk.fromHexString(hex));
		return reader.getVarInt();
	}
	
	@Test
	public void getVarIntBeyondEof() {
		
	}
	
	
}
