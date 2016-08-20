package com.simpleentity.db.log;

import static com.simpleentity.db.log.LogFileFormat.HEADER_TAG;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.CRC32;

import javax.annotation.CheckForNull;

import com.simpleentity.log.LogFile;
import com.simpleentity.util.ByteChunk;

// TODO consider moving reading and writing of transaction to log project
public class TransactionReader implements Iterator<ByteChunk> {

	private final long size;
	private final LogFile logFile;
	
	private long offset;
	@CheckForNull
	private ByteChunk next;

	public TransactionReader(LogFile logFile) {
		this.logFile = logFile;
		this.size = logFile.size();
		this.offset = 0;
		scanNextTransaction();
	}

	private void scanNextTransaction() {
		if (offset >= size) {
			next = null;
			return;
		}
		long startOffset = offset;
		
		// TODO Benchmark and optimize.
		ByteChunk tag = logFile.read(offset, HEADER_TAG.getLength());
		if (!tag.equals(LogFileFormat.HEADER_TAG)) {
			throw new DataCorruptionException("Corrupt header tag found in log file " + logFile + " at offset "
					+ offset + "! Expected " + HEADER_TAG + " but found " + tag);
		}
		offset += tag.getLength();

		CRC32 crc = new CRC32();
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		logFile.read(offset, buffer);
		buffer.flip();
		int size = buffer.getInt();
		if (size <= 0) {
			throw new DataCorruptionException("Corrupt transaction size found in log file " + logFile + " at offset "
					+ offset + ": " + size);
		}

		offset += buffer.capacity();
		ByteChunk data = logFile.read(offset, size);

		for (int i = 0; i < buffer.capacity(); ++i) {
			crc.update(buffer.get(i));
		}
		data.appendTo(crc);

		offset += size;
		buffer.flip();
		logFile.read(offset, buffer);
		buffer.flip();
		long expectedCrc = buffer.getInt() & 0xffffffffL;
		long storedCrc = crc.getValue();
		if (expectedCrc != storedCrc) {
			throw new DataCorruptionException("Corrupt transaction data found in log file " + logFile + " at offset "
					+ startOffset + "! CRC missmatch: " + expectedCrc + " != " + storedCrc);
		}
		
		offset += size;
		
		next = data;

		// TODO add padding to offset
	}

	@Override
	public boolean hasNext() {
		return offset != -1;
	}

	@Override
	public ByteChunk next() {
		if (next == null) {
			throw new NoSuchElementException();
		}
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
