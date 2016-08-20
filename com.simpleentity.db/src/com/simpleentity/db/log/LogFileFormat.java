package com.simpleentity.db.log;

import com.simpleentity.util.ByteChunk;

/**
 * File format:
 * 
 * ||HEADER_TAG|size|data|crc||padding||HEADER_TAG|...
 * |-----4-------4----N----4--|
 * 
 * Size is stored as signed 32bit little-endian integer. CRC covers size and
 * data bytes and is stored as unsigned 32bit little-endian integer.
 **/
public class LogFileFormat {

	public static ByteChunk HEADER_TAG = ByteChunk.newBuilder(4)
			.appendByte(161)
			.appendByte(184)
			.appendByte(152)
			.appendByte(105)
			.build();

}
