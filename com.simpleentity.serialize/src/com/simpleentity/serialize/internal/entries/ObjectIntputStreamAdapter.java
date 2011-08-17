/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;


import com.simpleentity.serialize.SerializationError;
import com.simpleentity.serialize.context.IObjectReader;
import com.simpleentity.util.io.ByteBufferReader;
import com.simpleentity.util.io.IOUtil;

/**
 * Adapter from the deserialization context to Java's {@link ObjectInputStream}.
 * 
 * @see CustomReadWriteEntry
 * 
 * @author micha
 */
public class ObjectIntputStreamAdapter extends ObjectInputStream {

  private static class GetFieldAdapter extends GetField {

    private final Map<String, Object> fMap;

    public GetFieldAdapter(Map<String, Object> map) {
      fMap = map;
    }

    @Override
    public boolean defaulted(String name) {
      return false;
    }

    @Override
    public boolean get(String name, boolean val) {
      return (Boolean) fMap.get(name);
    }

    @Override
    public byte get(String name, byte val) {
      return (Byte) fMap.get(name);
    }

    @Override
    public char get(String name, char val) {
      return (Character) fMap.get(name);
    }

    @Override
    public short get(String name, short val) {
      return (Short) fMap.get(name);
    }

    @Override
    public int get(String name, int val) {
      return (Integer) fMap.get(name);
    }

    @Override
    public long get(String name, long val) {
      return (Long) fMap.get(name);
    }

    @Override
    public float get(String name, float val) {
      return (Float) fMap.get(name);
    }

    @Override
    public double get(String name, double val) {
      return (Double) fMap.get(name);
    }

    @Override
    public Object get(String name, Object val) {
      return fMap.get(name);
    }

    @Override
    public ObjectStreamClass getObjectStreamClass() {
      throw new SerializationError("not supported");
    }

  }

  private final IObjectReader    fObjectReader;
  private final ByteBufferReader fStream;

  public ObjectIntputStreamAdapter(IObjectReader objectReader, ByteBufferReader stream) throws IOException {
    fObjectReader = objectReader;
    fStream = stream;
  }

  // DataInput

  @Override
  public void readFully(byte b[]) {
    fStream.read(b);
  }

  @Override
  public void readFully(byte b[], int off, int len) {
    fStream.read(b, off, len);
  }

  @Override
  public int skipBytes(int n) {
    fStream.read(new byte[n]);
    return n;
  }

  @Override
  public boolean readBoolean() {
    return fStream.readBoolean();
  }

  @Override
  public byte readByte() {
    return fStream.readByte();
  }

  @Override
  public int readUnsignedByte() {
    return fStream.readUnsignedByte();
  }

  @Override
  public short readShort() {
    return fStream.readShort();
  }

  @Override
  public int readUnsignedShort() {
    return fStream.readUnsignedShort();
  }

  @Override
  public char readChar() {
    return fStream.readChar();
  }

  @Override
  public int readInt() {
    return fStream.readInt();
  }

  @Override
  public long readLong() {
    return fStream.readLong();
  }

  @Override
  public float readFloat() {
    return fStream.readFloat();
  }

  @Override
  public double readDouble() {
    return fStream.readDouble();
  }

  @Override
  @Deprecated
  public String readLine() {
    throw new SerializationError("not supported");
  }

  /**
   * NOTE: Not as described in interface.
   */
  @Override
  public String readUTF() {
    return IOUtil.readStringUTF8(fStream);
  }

  // ObjectInput

  @Override
  protected Object readObjectOverride() {
    return fObjectReader.readObject(fStream);
  }

  @Override
  public int read() {
    return fStream.readUnsignedByte();
  }

  @Override
  public int read(byte b[]) {
    fStream.read(b);
    return b.length;
  }

  @Override
  public int read(byte b[], int off, int len) {
    fStream.read(b, off, len);
    return len;
  }

  @Override
  public long skip(long n) {
    fStream.read(new byte[(int) n]);
    return n;
  }

  @Override
  public int available() {
    return fStream.remaining();
  }

  @Override
  public void close() {
    throw new SerializationError("close() may not be called during deserialization!");
  }

  // ObjectInputStream

  @Override
  public void defaultReadObject() {
  }

  @Override
  public GetField readFields() {
    int size = IOUtil.readIntCompact(fStream);
    HashMap<String, Object> map = new HashMap<String, Object>(size);

    for (int i = 0; i < size; ++i) {
      String key = IOUtil.readStringUTF8(fStream);
      Object value = fObjectReader.readObject(fStream);
      map.put(key, value);
    }

    return new GetFieldAdapter(map);
  }

  @Override
  public Object readUnshared() {
    throw new SerializationError("not supported");
  }

  @Override
  public void registerValidation(ObjectInputValidation obj, int prio) {
    throw new SerializationError("not supported");
  }

}
