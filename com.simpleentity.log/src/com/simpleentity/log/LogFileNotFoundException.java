package com.simpleentity.log;

public class LogFileNotFoundException extends LogFileException {

	private static final long serialVersionUID = 1L;

	public LogFileNotFoundException(String path) {
		super("No log file exists at path " + path);
	}

}
