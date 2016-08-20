package com.simpleentity.db.log;

public class DataCorruptionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DataCorruptionException(String message) {
		super(message);
	}

}
