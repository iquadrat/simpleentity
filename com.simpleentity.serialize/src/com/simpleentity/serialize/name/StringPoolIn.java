/*
 * Created on Dec 8, 2007
 *
 */
package com.simpleentity.serialize.name;

import java.util.*;

import com.simpleentity.util.io.*;

public class StringPoolIn {

  private final List<String> fEntries;

  private StringPoolIn(List<String> entryList) {
    fEntries = entryList;
  }

  public String getString(int id) {
    return fEntries.get(id);
  }

  public static StringPoolIn deserialize(ByteBufferReader reader) {
    int entries = IOUtil.readIntCompact(reader);
    List<String> entryList = new ArrayList<String>(entries);
    for(int i=0; i<entries; ++i) {
      entryList.add(IOUtil.readStringUTF8(reader));
    }
    return new StringPoolIn(entryList);
  }

}
