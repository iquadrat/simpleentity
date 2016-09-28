package com.simpleentity.db.log;

import com.simpleentity.util.bytes.ByteChunk;
import com.simpleentity.util.bytes.ByteWriter;

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

	public static ByteChunk HEADER_TAG = new ByteWriter(4)
			.putByte(161)
			.putByte(184)
			.putByte(152)
			.putByte(105)
			.build();

}
