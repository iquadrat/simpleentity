package com.simpleentity.log.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import com.simpleentity.log.LogFile;
import com.simpleentity.log.LogFileException;
import com.simpleentity.log.util.IOUtil;
import com.simpleentity.util.Assert;
import com.simpleentity.util.bytes.ByteChunk;
import com.simpleentity.util.bytes.ByteWriter;

public class LocalLogFile implements LogFile {

	public static class LocalLogFileException extends LogFileException {
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

	// private class ListenerHolder extends AbstractLink<ListenerHolder>
	// implements ListenerHandle {
	// final LogFileListener listener;
	//
	// ListenerHolder(LogFileListener listener) {
	// this.listener = listener;
	// }
	//
	// @Override
	// public void close() throws IOException {
	// listeners.remove(this);
	// }
	// }

	// private final IntrusiveLinkedCollection<ListenerHolder> listeners = new
	// IntrusiveLinkedCollection<>();
	private final File file;
	private final RandomAccessFile randomAccess;

	private State state;
	private long size;

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
			this.size = randomAccess.length();
		} catch (IOException e) {
			throw new LocalLogFileException(file, e);
		}
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public void close() {
		if (state == State.CLOSED) {
			return;
		}
		IOUtil.closeSilently(randomAccess);
		setState(State.CLOSED);
	}

	/**
	 * Notifies a change to the given {@code state} to all listeners.
	 */
	private void setState(State newState) {
		// State previousState = state;
		// for (ListenerHolder listener : listeners) {
		// listener.listener.stateChanged(previousState, newState);
		// }
		state = newState;
	}

	@Override
	public ByteChunk read(long offset, int length) {
		// TODO consider avoiding double copy
		ByteBuffer bytes = ByteBuffer.allocate(length);
		read(offset, bytes);
		bytes.flip();
		return new ByteWriter().put(bytes).build();
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
			// FIXME check against reading beyond file end
			// FIXME check that buffer was fully filled
			randomAccess.getChannel().read(buffer, offset);
		} catch (IOException e) {
			throw new LocalLogFileException(file, e);
		}
	}

	@Override
	public void append(ByteChunk byteChunk) {
		try {
			int toWrite = byteChunk.getLength();
			int written = randomAccess.getChannel().write(ByteChunk.toByteBuffer(byteChunk), size);
			Assert.isTrue(toWrite == written, "Did not write as many bytes as expected: ", toWrite, " != ", written);
		} catch (IOException e) {
			throw new LocalLogFileException(file, e);
		}
	}

	@Override
	public int getBlockSize() {
		return 1;
	}

	@Override
	public long size() {
		try {
			return randomAccess.length();
		} catch(IOException e) {
			throw new LocalLogFileException(file, e);
		}
	}

	// @Override
	// public ListenerHandle listen(long offset, LogFileListener listener) {
	// ListenerHolder handle = new ListenerHolder(listener);
	// listeners.insertBack(handle);
	// return handle;
	// }

	// @Override
	// public void freeze() {
	// lock.lock();
	// try {
	// switch(state) {
	// case CLOSED:
	// throw new LocalLogFileException(file, "is closed");
	// case FROZEN:
	// throw new LocalLogFileException(file, "is already frozen");
	// }
	// if (!file.setWritable(false)) {
	// throw new LocalLogFileException(file, "Could not freeze");
	// }
	// setState(State.FROZEN);
	// } finally {
	// lock.unlock();
	// }
	// }

}
