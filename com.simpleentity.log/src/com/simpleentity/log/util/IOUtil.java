package com.simpleentity.log.util;

import java.io.Closeable;
import java.io.IOException;

import com.simpleentity.util.logger.Logger;

public class IOUtil {

	private static final Logger logger = Logger.forClass(IOUtil.class);

	public static final void closeSilently(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			// Swallow.
			logger.logVerbose(e);
		}
	}

	private IOUtil() {
	}

}
