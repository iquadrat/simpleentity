/*
 * Created on Dec 8, 2007
 *
 */
package com.simpleentity.serialize.name;

import java.util.*;

import com.simpleentity.util.io.*;

public class ClassNameRepositoryOut {

  private final StringPoolOut          fStringPool  = new StringPoolOut();
  private final Map<Class<?>, Integer> fClass2IdMap = new LinkedHashMap<Class<?>, Integer>();

  public int getIdForClass(Class<?> clazz) {
    Integer id = fClass2IdMap.get(clazz);
    if (id == null) {
      int newId = fClass2IdMap.size();
      fClass2IdMap.put(clazz, newId);
      registerStringParts(clazz);
      return newId;
    }
    return id;
  }

  public void serialize(ByteBufferWriter writer) {
    fStringPool.serialize(writer);
    IOUtil.writeIntCompact(writer, fClass2IdMap.size());
    for (Class<?> clazz: fClass2IdMap.keySet()) {
      writeEntryFor(writer, clazz);
    }
  }

  private void writeEntryFor(ByteBufferWriter writer, Class<?> clazz) {
    String[] parts = getNameParts(clazz);
    IOUtil.writeIntCompact(writer, parts.length);
    for (String part: parts) {
      int id = fStringPool.getId(part);
      IOUtil.writeIntCompact(writer, id);
    }
  }

  private void registerStringParts(Class<?> clazz) {
    String[] parts = getNameParts(clazz);
    for (String part: parts) {
      fStringPool.getId(part);
    }
  }

  private String[] getNameParts(Class<?> clazz) {
    String name = clazz.getName();
    String[] parts = name.split("\\.");
    return parts;
  }

}
