package com.simpleentity.util.bytes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link ByteWriter}.
 */
public class ByteWriterTest {
	@Test
	public void putPositiveVarInt() {
		assertEquals(ByteChunk.fromHexString("80"), encodePositiveVarInt(0));
		assertEquals(ByteChunk.fromHexString("81"), encodePositiveVarInt(1));
		assertEquals(ByteChunk.fromHexString("aa"), encodePositiveVarInt(42));
		assertEquals(ByteChunk.fromHexString("ff"), encodePositiveVarInt(127));
		assertEquals(ByteChunk.fromHexString("4080"), encodePositiveVarInt(128));
		assertEquals(ByteChunk.fromHexString("5234"), encodePositiveVarInt(0x1234));
		assertEquals(ByteChunk.fromHexString("7fff"), encodePositiveVarInt(0x3fff));
		assertEquals(ByteChunk.fromHexString("204001"), encodePositiveVarInt(0x4001));
		assertEquals(ByteChunk.fromHexString("3fffff"), encodePositiveVarInt(0x1fffff));
		assertEquals(ByteChunk.fromHexString("10200000"), encodePositiveVarInt(0x200000));
		assertEquals(ByteChunk.fromHexString("1fffffff"), encodePositiveVarInt(0x0fffffff));
		assertEquals(ByteChunk.fromHexString("0810000000"), encodePositiveVarInt(0x10000000));
		assertEquals(ByteChunk.fromHexString("087fffffff"), encodePositiveVarInt(Integer.MAX_VALUE));
		assertEquals(ByteChunk.fromHexString("041234567890"), encodePositiveVarInt(0x1234567890L));
		assertEquals(ByteChunk.fromHexString("052345678901"), encodePositiveVarInt(0x12345678901L));
		assertEquals(ByteChunk.fromHexString("02123456789012"), encodePositiveVarInt(0x123456789012L));
		assertEquals(ByteChunk.fromHexString("03234567890123"), encodePositiveVarInt(0x1234567890123L));
		assertEquals(ByteChunk.fromHexString("0112345678901234"), encodePositiveVarInt(0x12345678901234L));
		assertEquals(ByteChunk.fromHexString("008123456789012345"), encodePositiveVarInt(0x123456789012345L));
		assertEquals(ByteChunk.fromHexString("00ffffffffffffffff"), encodePositiveVarInt(Long.MAX_VALUE));
	}

	private ByteChunk encodePositiveVarInt(long n) {
		return new ByteWriter().putPositiveVarInt(n).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void putPositiveVarIntNegativeThrows() {
		encodePositiveVarInt(-1);
	}

	@Test
	public void putSignedVarInt() {
		assertEquals(ByteChunk.fromHexString("80"), encodeSignedVarInt(0));
		assertEquals(ByteChunk.fromHexString("82"), encodeSignedVarInt(1));
		assertEquals(ByteChunk.fromHexString("fe"), encodeSignedVarInt(63));
		assertEquals(ByteChunk.fromHexString("4080"), encodeSignedVarInt(64));
		assertEquals(ByteChunk.fromHexString("4fc0"), encodeSignedVarInt(2016));

		assertEquals(ByteChunk.fromHexString("81"), encodeSignedVarInt(-1));
		assertEquals(ByteChunk.fromHexString("83"), encodeSignedVarInt(-2));
		assertEquals(ByteChunk.fromHexString("fd"), encodeSignedVarInt(-63));
		assertEquals(ByteChunk.fromHexString("ff"), encodeSignedVarInt(-64));
		assertEquals(ByteChunk.fromHexString("4081"), encodeSignedVarInt(-65));
		assertEquals(ByteChunk.fromHexString("204e1f"), encodeSignedVarInt(-10000));

		assertEquals(ByteChunk.fromHexString("08fffffffe"), encodeSignedVarInt(Integer.MAX_VALUE));
		assertEquals(ByteChunk.fromHexString("08ffffffff"), encodeSignedVarInt(Integer.MIN_VALUE));

		assertEquals(ByteChunk.fromHexString("0040fffffffffffffffe"), encodeSignedVarInt(Long.MAX_VALUE));
		assertEquals(ByteChunk.fromHexString("0040ffffffffffffffff"), encodeSignedVarInt(Long.MIN_VALUE));
	}

	private ByteChunk encodeSignedVarInt(long n) {
		return new ByteWriter().putSignedVarInt(n).build();
	}

}
