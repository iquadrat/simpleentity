package com.simpleentity.log;

import java.io.Closeable;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface LogFile extends Closeable {
	
	public enum State {
		CLOSED,
		APPENDABLE,
		FROZEN
	}
	
	public int getBlockSize();
	
	public State getState();
	
	// TODO add async read/write operation support

	public InputStream read(long offset);
	
	public void read(long offset, ByteBuffer buffer);
	
	public ListenerHandle listen(long offset, LogFileListener listener);
	
	public void append(ByteBuffer buffer);
	
	public void freeze();
	
}
