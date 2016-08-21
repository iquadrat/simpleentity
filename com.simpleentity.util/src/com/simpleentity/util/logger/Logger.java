package com.simpleentity.util.logger;

import com.simpleentity.util.logger.LogHandler.LogLevel;

/**
 * Default logging implementation.
 * 
 * @author micha
 */
public class Logger {

	private static Logger instance = new Logger();

	private LogHandler handler = new StdOutLogHandler();

	
	public static Logger getRootLogger() {
		return instance;
	}
	
	// TODO create a forCallingClass() method.

	public static Logger forClass(Class<?> class_) {
		// TODO create sub-loggers that remember the logging source.
		return getRootLogger();
	}

	/**
	 * Logs the given <code>message</code>.
	 */
	public void logFatal(String message) {
		handler.handle(LogLevel.FATAL, message, null);
	}

	/**
	 * Logs the given <code>throwable</code>.
	 */
	public void logFatal(Throwable throwable) {
		handler.handle(LogLevel.FATAL, null, throwable);
	}

	/**
	 * Logs the given <code>message</code>.
	 */
	public void logError(String message) {
		handler.handle(LogLevel.ERROR, message, null);
	}

	/**
	 * Logs the given <code>throwable</code>.
	 */
	public void logError(Throwable throwable) {
		handler.handle(LogLevel.ERROR, null, throwable);
	}

	public void logInfo(Throwable throwable) {
		handler.handle(LogLevel.INFO, null, throwable);
	}

	public void logInfo(String message) {
		handler.handle(LogLevel.INFO, message, null);
	}

	public void logVerbose(Throwable throwable) {
		handler.handle(LogLevel.VERBOSE, null, throwable);
	}

	public void logVerbose(String message) {
		handler.handle(LogLevel.VERBOSE, message, null);
	}

	/**
	 * Replaces the log handler by the given <code>handler</code>.
	 * 
	 * @return the old handler
	 */
	public LogHandler setLogHandler(LogHandler handler) {
		LogHandler oldHandler = handler;
		this.handler = handler;
		return oldHandler;
	}
}
