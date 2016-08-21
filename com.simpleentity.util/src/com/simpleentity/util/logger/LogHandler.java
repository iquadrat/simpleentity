package com.simpleentity.util.logger;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public interface LogHandler {

	public enum LogLevel {
		FATAL, 
		ERROR,
		WARNING,
		INFO,
		VERBOSE,
	}
	
	public void handle(LogLevel level, @CheckForNull String message, @CheckForNull Throwable throwable);
}
