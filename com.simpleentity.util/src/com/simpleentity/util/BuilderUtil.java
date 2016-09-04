package com.simpleentity.util;

import javax.annotation.CheckForNull;

public class BuilderUtil {

	public static <T> T requiredBuilderField(String field, @CheckForNull T object) {
		if (object == null) {
			throw new IllegalStateException("Field " + field + " not set while building!");
		}
		return object;
	}

	public static long positiveLong(String field, long value) {
		if (value < 0) {
			throw new IllegalStateException("Field " + field + " not set while building!");
		}
		return value;
	}

	private BuilderUtil() {
	}

}
