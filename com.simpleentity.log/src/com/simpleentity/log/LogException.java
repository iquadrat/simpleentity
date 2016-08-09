package com.simpleentity.log;

public class LogException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public LogException(String message) {
		super(message);
	}
	
	public LogException(Throwable cause) {
		super(cause);
	}
	
	public LogException(String message, Throwable cause) {
		super(message, cause);
	}

}
