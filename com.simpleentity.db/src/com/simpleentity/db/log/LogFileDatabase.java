package com.simpleentity.db.log;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.simpleentity.db.Database;
import com.simpleentity.db.session.Session;
import com.simpleentity.db.session.SessionListener;
import com.simpleentity.log.LogFile;
import com.simpleentity.log.LogFileException;
import com.simpleentity.log.LogFileManager;
import com.simpleentity.log.LogFileNotFoundException;
import com.simpleentity.util.ByteChunk;
import com.simpleentity.util.collection.ListenerHandle;
import com.simpleentity.util.collection.ListenerList;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public class LogFileDatabase implements Database {
	
	private final ListenerList<SessionListener> sessionListeners = new ListenerList<>();
	private final LogFileManager logFileManager;
	private final Options options;
	private final LogFile currentLog;
	
	public LogFileDatabase(LogFileManager logFileManager, Options options) throws LogFileException {
		this.logFileManager = logFileManager;
		this.options = options;
		this.currentLog = initialize();
	}
	
	// TODO consider making static
	private LogFile initialize() throws LogFileException {
		// Scan through all log files.
		long splitFileNr = 0;
		LogFile last = null;
		while(true) {
			LogFile logFile = null;
			try {
				logFile = openSplitFile(splitFileNr);
				if (logFile == null) {
					break;
				}
				if (last != null) {
					last.close();
				}
				initializeFrom(logFile);
				last = logFile;
				logFile = null;
			} finally {
				if (logFile != null) {
					logFile.close();
				}
			}
		}
		return last;
	}

	private void initializeFrom(LogFile logFile) {
		TransactionReader reader = new TransactionReader(logFile);
		while(reader.hasNext()) {
			initializeAddTransaction(reader.next());
		}
	}

	private void initializeAddTransaction(ByteChunk data) {
		// TODO Auto-generated method stub
		
	}

	@CheckForNull
	private LogFile openSplitFile(long splitFileNr) {
		String fileName = getLogFileName(splitFileNr);
		try {
			return logFileManager.open(options.getDbRoot() + "/" + fileName);
		} catch(LogFileNotFoundException e) {
			return null;
		}
	}

	private String getLogFileName(long splitFileNr) {
		return options.getDbPrefix() + String.format("%06d", splitFileNr);
	}

	public ListenerHandle addSessionListener(SessionListener transactionListener) {
		return sessionListeners.add(transactionListener);
	}
	
	public Session createHeadSession() {
		return null; // TODO
	}
	
}
