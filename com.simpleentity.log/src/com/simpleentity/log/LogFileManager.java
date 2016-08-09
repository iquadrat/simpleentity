package com.simpleentity.log;


public interface LogFileManager {
	
	public LogFile open(String path);
	
	public void delete(String path);

}
