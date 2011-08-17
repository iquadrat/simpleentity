/*
 * Created on Dec 8, 2007
 *
 */
package com.simpleentity.serialize.name;

import java.util.*;


import com.simpleentity.serialize.SerializationError;
import com.simpleentity.util.io.*;

public class ClassNameRepositoryIn {

  private final List<Class<?>> fClassList;

  private ClassNameRepositoryIn(String[] names) throws ClassNotFoundException {
    fClassList = new ArrayList<Class<?>>(names.length);
    for(String name: names) {
      fClassList.add(Class.forName(name));
    }
  }

  public Class<?> getClassForId(int id) {
    return fClassList.get(id);
  }

  public static ClassNameRepositoryIn deserialize(ByteBufferReader reader) {
    StringPoolIn stringPool = StringPoolIn.deserialize(reader);
    String[] names = readNames(reader, stringPool);
    try {
      return new ClassNameRepositoryIn(names);
    } catch (ClassNotFoundException e) {
      throw new SerializationError(e);
    }
  }

  private static String[] readNames(ByteBufferReader reader, StringPoolIn stringPool) {
    int count = IOUtil.readIntCompact(reader);
    String[] result = new String[count];
    for(int i=0; i<count; ++i) {
      result[i] = readName(stringPool, reader);
    }
    return result;
  }

  private static String readName(StringPoolIn stringPool, ByteBufferReader reader) {
    int parts = IOUtil.readIntCompact(reader);
    StringBuilder builder = new StringBuilder();
    for(int i=0; i<parts; ++i) {
      int id = IOUtil.readIntCompact(reader);
      builder.append(stringPool.getString(id));
      if (i != parts-1) {
        builder.append(".");
      }
    }
    return builder.toString();
  }

}
