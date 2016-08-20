package com.simpleentity.log;

import java.io.Closeable;
import java.io.InputStream;
import java.nio.ByteBuffer;

import net.jcip.annotations.NotThreadSafe;

import com.simpleentity.util.ByteChunk;

@NotThreadSafe
public interface LogFile extends Closeable {
	
	public enum State {
		CLOSED,
		APPENDABLE,
		FROZEN
	}
	
	public int getBlockSize();
	
	public State getState();
	
	public InputStream read(long offset) throws LogFileException;
	
	public ByteChunk read(long offset, int length) throws LogFileException;;
	
	public void read(long offset, ByteBuffer buffer) throws LogFileException;
	
	// TODO does append flush? Should a flush() be added?
	public void append(ByteChunk byteChunk) throws LogFileException;
	
	public void close();
	
	public long size();
	
	// TODO add async read/write operation support
	
//	public ListenerHandle listen(long offset, LogFileListener listener);
	
//	public void freeze();
	
}
