package com.simpleentity.util.bytes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link ByteWriter}.
 */
public class ByteWriterTest {
	@Test
	public void putVarInt() {
		assertEquals(ByteChunk.fromHexString("80"), encodeVarInt(0));
		assertEquals(ByteChunk.fromHexString("81"), encodeVarInt(1));
		assertEquals(ByteChunk.fromHexString("aa"), encodeVarInt(42));
		assertEquals(ByteChunk.fromHexString("ff"), encodeVarInt(127));
		assertEquals(ByteChunk.fromHexString("4080"), encodeVarInt(128));
		assertEquals(ByteChunk.fromHexString("5234"), encodeVarInt(0x1234));
		assertEquals(ByteChunk.fromHexString("7fff"), encodeVarInt(0x3fff));
		assertEquals(ByteChunk.fromHexString("204001"), encodeVarInt(0x4001));
		assertEquals(ByteChunk.fromHexString("3fffff"), encodeVarInt(0x1fffff));
		assertEquals(ByteChunk.fromHexString("10200000"), encodeVarInt(0x200000));
		assertEquals(ByteChunk.fromHexString("1fffffff"), encodeVarInt(0x0fffffff));
		assertEquals(ByteChunk.fromHexString("0810000000"), encodeVarInt(0x10000000));
		assertEquals(ByteChunk.fromHexString("087fffffff"), encodeVarInt(Integer.MAX_VALUE));
		assertEquals(ByteChunk.fromHexString("041234567890"), encodeVarInt(0x1234567890L));
		assertEquals(ByteChunk.fromHexString("052345678901"), encodeVarInt(0x12345678901L));
		assertEquals(ByteChunk.fromHexString("02123456789012"), encodeVarInt(0x123456789012L));
		assertEquals(ByteChunk.fromHexString("03234567890123"), encodeVarInt(0x1234567890123L));
		assertEquals(ByteChunk.fromHexString("0112345678901234"), encodeVarInt(0x12345678901234L));
		assertEquals(ByteChunk.fromHexString("008123456789012345"), encodeVarInt(0x123456789012345L));
		assertEquals(ByteChunk.fromHexString("00ffffffffffffffff"), encodeVarInt(Long.MAX_VALUE));
	}

	private ByteChunk encodeVarInt(int n) {
		return new ByteWriter().putVarInt(n).build();
	}

	private ByteChunk encodeVarInt(long n) {
		return new ByteWriter().putVarInt(n).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void putVarIntNegativeThrows() {
		encodeVarInt(-1);
	}
}
