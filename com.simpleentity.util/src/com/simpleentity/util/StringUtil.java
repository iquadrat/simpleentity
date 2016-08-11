package com.simpleentity.util;


public class StringUtil {

	private StringUtil() {
	}

	/**
	 * Splits a file name in name and extension.
	 * 
	 * @param name
	 *            the file name to split
	 * @return an array of two elements: The file name and the extension.
	 */
	public static String[] getFileNameAndExtension(String name) {
		int lastDot = name.lastIndexOf('.');
		if (lastDot == -1) {
			return new String[] { name, "" };
		}
		String prefix = name.substring(0, lastDot);
		String postfix = name.substring(lastDot + 1);
		return new String[] { prefix, postfix };
	}

	public static String bytesToHex(byte[] bytes) {
		return bytesToHex(bytes, 0, bytes.length);
	}

	public static String bytesToHex(byte[] bytes, int offset, int length) {
		StringBuilder result = new StringBuilder(length * 2);
		for (int i = offset; i < offset + length; ++i) {
			byte b = bytes[i];
			result.append(halfByteToChar((b & 0xff) >>> 4));
			result.append(halfByteToChar((b & 15)));
		}
		return result.toString();
	}
	
	private static char halfByteToChar(int halfByte) {
		if (halfByte < 10) {
			return (char) ('0' + halfByte);
		}
		return (char) ('a' + (halfByte - 10));
	}

}
