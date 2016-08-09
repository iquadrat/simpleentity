package com.simpleentity.log;

import java.nio.file.Path;

public interface LogFileManager {
	
	public LogFile open(Path path);
	
	public void delete(Path path);

}
