package com.simpleentity.util;

public class StringUtil {

  private StringUtil() {}

  /**
   * Splits a file name in name and extension.
   * @param name the file name to split
   * @return an array of two elements: The file name and the extension.
   */
  public static String[] getFileNameAndExtension(String name) {
    int lastDot = name.lastIndexOf('.');
    if (lastDot == -1) {
      return new String[] {name, ""};
    }
    String prefix = name.substring(0, lastDot);
    String postfix = name.substring(lastDot + 1);
    return new String[] {prefix, postfix};
  }

}
