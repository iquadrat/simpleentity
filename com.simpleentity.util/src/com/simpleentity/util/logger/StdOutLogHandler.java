package com.simpleentity.util.logger;

import java.io.PrintStream;

/**
 * Implementation that writes messages to standard output and standard error.
 */
public class StdOutLogHandler implements LogHandler {
	@Override
	public void handle(LogLevel level, String message, Throwable throwable) {
		PrintStream out = getOutputStream(level);
		if (message != null) {
			out.println(message);
		} else {
			out.println("An exception occured");
		}
		if (throwable != null) {
			out.println(throwable.getMessage());
			throwable.printStackTrace(out);
		}
	}

	private PrintStream getOutputStream(LogLevel level) {
		switch (level) {
		case ERROR:
		case FATAL:
			return System.err;
		default:
			return System.out;
		}
	}
}