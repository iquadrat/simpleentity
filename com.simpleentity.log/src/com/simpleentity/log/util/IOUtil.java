package com.simpleentity.log.util;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {
	
	public static final void closeSilently(Closeable closeable) {
		try {
			closeable.close();
		} catch(IOException e) {
			// Swallow.
		}
	}
	
	private IOUtil() {
	}

}
