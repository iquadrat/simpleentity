package com.simpleentity.log.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.IllegalSelectorException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Generated;

import org.povworld.collection.mutable.AbstractIntrusiveLinkedCollection.AbstractLink;
import org.povworld.collection.mutable.IntrusiveLinkedCollection;

import com.simpleentity.log.ListenerHandle;
import com.simpleentity.log.LogException;
import com.simpleentity.log.LogFile;
import com.simpleentity.log.LogFileListener;
import com.simpleentity.log.util.IOUtil;

public class LocalLogFile implements LogFile {
	
	public static class LocalLogFileException extends LogException {
		private static final long serialVersionUID = 1L;
		private final File file;

		private LocalLogFileException(File file, String message) {
			super(message + ", file=" + file);
			this.file = file;
		}

		private LocalLogFileException(File file, Throwable t) {
			super("file=" + file, t);
			this.file = file;
		}
		
		public File getFile() {
			return file;
		}
	}
	
	private class ListnerHolder extends AbstractLink<ListnerHolder> implements ListenerHandle {
		final LogFileListener listener;

		ListnerHolder(LogFileListener listener) {
			this.listener = listener;
		}

		@Override
		public void close() throws IOException {
			listeners.remove(this);
		}
	}
	
	private final Lock lock = new ReentrantLock();

	private final IntrusiveLinkedCollection<ListnerHolder> listeners = new IntrusiveLinkedCollection<>();
	private final File file;
	private final RandomAccessFile randomAccess;
	
	// Guarded by lock
	private State state;

	public LocalLogFile(File file) {
		if (!file.exists()) {
			throw new LocalLogFileException(file, "File does not exist");
		}
		if (!file.canRead()) {
			throw new LocalLogFileException(file, "File is not readable");
		}
		this.file = file;
		this.state = file.canWrite() ? State.APPENDABLE : State.FROZEN;
		try {
			this.randomAccess = new RandomAccessFile(file, state == State.APPENDABLE ? "rw" : "r");
		} catch (FileNotFoundException e) {
			throw new LocalLogFileException(file, e);
		}
	}
	
	@Override
	public State getState() {
		lock.lock();
		try {
			return state;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void close() throws IOException {
		lock.lock();
		try{
			if (state == State.CLOSED) {
				return;
			}
			IOUtil.closeSilently(randomAccess);
			setState(State.CLOSED);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void freeze() {
		lock.lock();
		try {
			switch(state) {
			case CLOSED:
				throw new LocalLogFileException(file, "is closed");
			case FROZEN:
				throw new LocalLogFileException(file, "is already frozen");
			}
			if (!file.setWritable(false)) {
				throw new LocalLogFileException(file, "Could not freeze");
			}
			setState(State.FROZEN);
		} finally {
			lock.unlock();
		}
	}


	/**
	 * Notifies a change to the given {@code state} to all listeners.
	 * Must be called with {@link #lock} held.
	 */
	private void setState(State newState) {
		State previousState = state;
		for(ListnerHolder listener: listeners) {
			listener.listener.stateChanged(previousState, newState);
		}
	}


	@Override
	public InputStream read(long offset) {
		try {
			FileInputStream stream = new FileInputStream(file);
			stream.skip(offset);
			return stream;
		} catch (IOException e) {
			throw new LocalLogFileException(file, e);
		}
	}

	@Override
	public void read(long offset, ByteBuffer buffer) {
		try {
			randomAccess.getChannel().read(buffer, offset);
		} catch (IOException e) {
			throw new LocalLogFileException(file, e);
		}
	}

	@Override
	public ListenerHandle listen(long offset, LogFileListener listener) {
		ListnerHolder handle = new ListnerHolder(listener);
		listeners.insertBack(handle);
		return handle;
	}

	@Override
	public void append(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBlockSize() {
		return 1;
	}

}
