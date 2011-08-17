/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import com.simpleentity.serialize.SerializationError;
import com.simpleentity.serialize.context.IObjectWriter;
import com.simpleentity.util.io.ByteBufferWriter;
import com.simpleentity.util.io.IOUtil;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * Adapter from the serialization context to Java's {@link ObjectOutputStream}.
 *
 * @see CustomReadWriteEntry
 *
 * @author micha
 */
public class ObjectOutputStreamAdapter extends ObjectOutputStream {

  private static class PutFieldAdapter extends PutField {

    private final Map<String, Object> fFieldMap = new HashMap<String, Object>();

    @Override
    public void put(String name, boolean val) {
      fFieldMap.put(name, val);
    }

    @Override
    public void put(String name, byte val) {
      fFieldMap.put(name, val);
    }

    @Override
    public void put(String name, char val) {
      fFieldMap.put(name, val);
    }

    @Override
    public void put(String name, short val) {
      fFieldMap.put(name, val);
    }

    @Override
    public void put(String name, int val) {
      fFieldMap.put(name, val);
    }

    @Override
    public void put(String name, long val) {
      fFieldMap.put(name, val);
    }

    @Override
    public void put(String name, float val) {
      fFieldMap.put(name, val);
    }

    @Override
    public void put(String name, double val) {
      fFieldMap.put(name, val);
    }

    @Override
    public void put(String name, Object val) {
      fFieldMap.put(name, val);
    }

    @Override
    @Deprecated
    public void write(ObjectOutput out) {
      throw new SerializationError("not supported");
    }

  }

  private final IObjectWriter fContext;
  private final ByteBufferWriter      fStream;

  @CheckForNull
  private PutFieldAdapter                   fPutFields;

  public ObjectOutputStreamAdapter(IObjectWriter context, ByteBufferWriter output) throws IOException {
    super();
    fContext = context; // TODO rename
    fStream = output;
  }

  // DataOutput

  @Override
  public void write(int b) {
    fStream.write(b);
  }

  @Override
  public void write(byte b[]) {
    fStream.write(b);
  }

  @Override
  public void write(byte b[], int off, int len) {
    fStream.write(b, off, len);
  }

  @Override
  public void writeBoolean(boolean v) {
    fStream.putBoolean(v);
  }

  @Override
  public void writeByte(int v) {
    fStream.write(v);
  }

  @Override
  public void writeShort(int v) {
    fStream.putShort((short) v);
  }

  @Override
  public void writeChar(int v) {
    fStream.putChar((char) v);
  }

  @Override
  public void writeInt(int v) {
    fStream.putInt(v);
  }

  @Override
  public void writeLong(long v) {
    fStream.putLong(v);
  }

  @Override
  public void writeFloat(float v) {
    fStream.putFloat(v);
  }

  @Override
  public void writeDouble(double v) {
    fStream.putDouble(v);
  }

  /**
   * NOTE: Not as in the interface description!
   */
  @Override
  public void writeBytes(String s) {
    fStream.putByteArray(s.getBytes());
  }

  /**
   * NOTE: Not as in the interface description!
   */
  @Override
  public void writeChars(String s) {
    char[] chars = new char[s.length()];
    s.getChars(0, s.length(), chars, 0);
    fStream.putCharArray(chars);
  }

  /**
   * NOTE: Not as in the interface description!
   */
  @Override
  public void writeUTF(String s) {
    IOUtil.writeStringUTF8(fStream, s);
  }

  // ObjectOutput

  @Override
  protected void writeObjectOverride(Object object) {
    fContext.writeObject(fStream, object);
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() {
    throw new SerializationError("close() may not be called during serialization!");
  }

  // ObjectOutputStream

  @Override
  public void defaultWriteObject() {
  }

  @Override
  public PutField putFields() {
    if (fPutFields == null) {
      fPutFields = new PutFieldAdapter();
    }
    return fPutFields;
  }

  @Override
  public void reset() {
    throw new SerializationError("reset() may not be called during serialization!");
  }

  @Override
  public void writeFields() {
    if (fPutFields == null) {
      fPutFields = new PutFieldAdapter();
    }

    IOUtil.writeIntCompact(fStream, fPutFields.fFieldMap.size());
    for(Entry<String, Object> entry: fPutFields.fFieldMap.entrySet()) {
      IOUtil.writeStringUTF8(fStream, entry.getKey());
      fContext.writeObject(fStream, entry.getValue());
    }
  }

  @Override
  public void writeUnshared(Object obj) {
    throw new SerializationError("not supported");
  }

}
