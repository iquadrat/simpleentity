package com.simpleentity.serialize2;

public class SerializerException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public SerializerException(String message) {
		super(message);
	}
	
	public SerializerException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public SerializerException(Throwable throwable) {
		super(throwable);
	}



}
