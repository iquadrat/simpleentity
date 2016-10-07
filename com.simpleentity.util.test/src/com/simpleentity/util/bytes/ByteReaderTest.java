package com.simpleentity.util.bytes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link ByteReader}.
 */
public class ByteReaderTest {
	@Test
	public void getPositiveVarInt() {
		assertEquals(0, decodePositiveVarInt("80"));
		assertEquals(1, decodePositiveVarInt("81"));
		assertEquals(42, decodePositiveVarInt("aa"));
		assertEquals(127, decodePositiveVarInt("ff"));
		assertEquals(128, decodePositiveVarInt("4080"));
		assertEquals(0x1234, decodePositiveVarInt("5234"));
		assertEquals(0x3fff, decodePositiveVarInt("7fff"));
		assertEquals(0x4001, decodePositiveVarInt("204001"));
		assertEquals(0x1fffff, decodePositiveVarInt("3fffff"));
		assertEquals(0x200000, decodePositiveVarInt("10200000"));
		assertEquals(0x0fffffff, decodePositiveVarInt("1fffffff"));
		assertEquals(0x10000000, decodePositiveVarInt("0810000000"));
		assertEquals(Integer.MAX_VALUE, decodePositiveVarInt("087fffffff"));
		assertEquals(0x1234567890L, decodePositiveVarInt("041234567890"));
		assertEquals(0x12345678901L, decodePositiveVarInt("052345678901"));
		assertEquals(0x123456789012L, decodePositiveVarInt("02123456789012"));
		assertEquals(0x1234567890123L, decodePositiveVarInt("03234567890123"));
		assertEquals(0x12345678901234L, decodePositiveVarInt("0112345678901234"));
		assertEquals(0x123456789012345L, decodePositiveVarInt("008123456789012345"));
		assertEquals(Long.MAX_VALUE, decodePositiveVarInt("00ffffffffffffffff"));
	}

	// Tests decoding of values that never result from correct encoding.
	@Test
	public void getPositiveVarIntImpossibleEncodings() {
		assertEquals(0, decodePositiveVarInt("4000"));
		assertEquals(3, decodePositiveVarInt("4003"));
		assertEquals(0x123, decodePositiveVarInt("200123"));
		assertEquals(0x123456, decodePositiveVarInt("008000000000123456"));
		assertEquals(0x123456, decodePositiveVarInt("00400000000000123456"));
	}

	@Test(expected = IllegalStateException.class)
	public void getPositiveVarIntOutOfLongRange() {
		decodePositiveVarInt("00410000000000000000000000");
	}

	private long decodePositiveVarInt(String hex) {
		ByteReader reader = new ByteReader(ByteChunk.fromHexString(hex));
		return reader.getPositiveVarInt();
	}

	@Test
	public void getSignedVarInt() {
		assertEquals(0, decodeSignedVarInt("80"));
		assertEquals(1, decodeSignedVarInt("82"));
		assertEquals(63, decodeSignedVarInt("fe"));
		assertEquals(64, decodeSignedVarInt("4080"));

		assertEquals(-1, decodeSignedVarInt("81"));
		assertEquals(-2, decodeSignedVarInt("83"));
		assertEquals(-63, decodeSignedVarInt("fd"));
		assertEquals(-64, decodeSignedVarInt("ff"));
		assertEquals(-65, decodeSignedVarInt("4081"));

		assertEquals(Integer.MAX_VALUE, decodeSignedVarInt("08fffffffe"));
		assertEquals(Integer.MIN_VALUE, decodeSignedVarInt("08ffffffff"));

		assertEquals(Long.MAX_VALUE, decodeSignedVarInt("0040fffffffffffffffe"));
		assertEquals(Long.MIN_VALUE, decodeSignedVarInt("0040ffffffffffffffff"));
	}

	private long decodeSignedVarInt(String hex) {
		ByteReader reader = new ByteReader(ByteChunk.fromHexString(hex));
		return reader.getSignedVarInt();
	}

	@Test(expected = IllegalStateException.class)
	public void getVarIntBeyondEof() {
		decodePositiveVarInt("011234");
	}
}
