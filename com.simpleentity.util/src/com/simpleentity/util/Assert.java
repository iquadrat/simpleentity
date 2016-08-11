/*
 * Created on 04.07.2007
 *
 */
package com.simpleentity.util;

import org.povworld.collection.common.ObjectUtil;

import com.simpleentity.util.logger.Logger;

public class Assert {

	private static final Logger logger = Logger.forClass(Assert.class);

	private Assert() {
		// utility class
	}

	public static void equals(short expected, short actual) {
		if (expected == actual) {
			return;
		}
		fail("Actual '", actual, "' is not equal to expected '", expected, "'");
	}

	public static void equals(int expected, int actual) {
		if (expected == actual) {
			return;
		}
		fail("Actual '", actual, "' is not equal to expected '", expected, "'");
	}

	public static void equals(long expected, long actual) {
		if (expected == actual) {
			return;
		}
		fail("Actual '", actual, "' is not equal to expected '", expected, "'");
	}

	public static void equals(Object expected, Object actual) {
		if (ObjectUtil.objectEquals(expected, actual)) {
			return;
		}
		fail("Actual '", actual, "' is not equal to expected '", expected, "'");
	}

	public static AssertionFailedError fail(Object... messageParts) {
		String message = toString(messageParts);
		AssertionFailedError failure = new AssertionFailedError(message);
		logger.logFatal(failure);
		throw failure;
	}

	public static AssertionFailedError fail(Throwable t) {
		AssertionFailedError failure = new AssertionFailedError(t);
		logger.logFatal(failure);
		throw failure;
	}

	public static void isFalse(boolean condition) {
		isFalse(condition, "Assertion failed!");
	}

	public static void isFalse(boolean condition, Object... message) {
		if (!condition) {
			return;
		}
		fail(message);
	}

	public static void isNotNull(Object o) {
		if (o != null) {
			return;
		}
		fail("Expected an object but was null!");
	}

	public static void isNotNull(Object o, Object... message) {
		if (o != null) {
			return;
		}
		fail(message);
	}

	public static void isNull(Object o) {
		if (o == null) {
			return;
		}
		fail("Expected null but was ", o, "!");
	}

	public static void isNull(Object o, Object... message) {
		if (o == null) {
			return;
		}
		fail(message);
	}

	public static void isTrue(boolean condition) {
		isTrue(condition, "Assertion failed!");
	}

	public static void isTrue(boolean condition, Object... message) {
		if (condition) {
			return;
		}
		fail(message);
	}

	private static String toString(Object[] message) {
		StringBuilder builder = new StringBuilder();
		for (Object o : message) {
			builder.append(o.toString());
		}
		return builder.toString();
	}

	public static void same(Object o1, Object o2) {
		if (o1 == o2) {
			return;
		}
		fail("Object " + o1 + " is not same as " + o2);
	}

	public static void log(String string) {
		logger.logInfo(string);
	}

}
