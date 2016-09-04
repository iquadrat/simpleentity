package com.simpleentity.util;

import java.nio.charset.Charset;



public class StringUtil {
	
	public static Charset UTF8 = Charset.forName("UTF-8");

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
	
	public static byte[] hex2Bytes(String string) {
		//PreConditions.argument(string).verify(string.length() % 2 == 0, "Expected an even number of characters.");
		int outputLength = string.length() / 2;
		byte[] result = new byte[outputLength];
		for (int i = 0; i < outputLength; ++i) {
			int resultIndex = i;
			result[resultIndex] = (byte) ((hex2Byte(string.charAt(2 * i)) << 4) | hex2Byte(string.charAt(2 * i + 1)));
		}
		return result;
	}

	private static byte hex2Byte(char c) {
		if (c >= '0' && c <= '9') {
			return (byte) (c - '0');
		}
		if (c >= 'a' && c <= 'f') {
			return (byte) (c - 'a' + 10);
		}
		if (c >= 'A' && c <= 'F') {
			return (byte) (c - 'A' + 10);
		}
		throw new IllegalArgumentException("Invalid character in hexadecimal string: " + c);
	}

	
	private static char halfByteToChar(int halfByte) {
		if (halfByte < 10) {
			return (char) ('0' + halfByte);
		}
		return (char) ('a' + (halfByte - 10));
	}

}
