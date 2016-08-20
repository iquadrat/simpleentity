package com.simpleentity.log;


public interface LogFileManager {

	public LogFile open(String path) throws LogFileException;

	public void delete(String path) throws LogFileException;

}
