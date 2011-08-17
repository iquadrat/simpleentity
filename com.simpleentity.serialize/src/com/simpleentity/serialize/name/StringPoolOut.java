/*
 * Created on Dec 8, 2007
 *
 */
package com.simpleentity.serialize.name;

import java.util.*;

import com.simpleentity.util.io.*;

public class StringPoolOut {

  private final Map<String, Integer> fString2IdMap = new LinkedHashMap<String, Integer>();

  public int getId(String string) {
    Integer id = fString2IdMap.get(string);
    if (id != null) {
      return id;
    }

    int newId = fString2IdMap.size();
    fString2IdMap.put(string, newId);
    return newId;
  }

  public void serialize(ByteBufferWriter writer) {
    IOUtil.writeIntCompact(writer, fString2IdMap.size());
    for(String string: fString2IdMap.keySet()) {
      IOUtil.writeStringUTF8(writer, string);
    }
  }

}
