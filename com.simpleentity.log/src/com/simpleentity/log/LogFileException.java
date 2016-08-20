package com.simpleentity.log;

public class LogFileException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public LogFileException(String message) {
		super(message);
	}
	
	public LogFileException(Throwable cause) {
		super(cause);
	}
	
	public LogFileException(String message, Throwable cause) {
		super(message, cause);
	}

}
