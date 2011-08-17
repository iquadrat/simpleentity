/*
 * Created on Dec 1, 2007
 *
 */
package com.simpleentity.serialize.context;

import java.nio.ByteBuffer;
import java.util.*;


import com.simpleentity.serialize.name.ClassNameRepositoryOut;
import com.simpleentity.serialize.object.*;
import com.simpleentity.util.io.*;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public class SerializationContext implements IObjectSerializationContext {

  private static final int                 DEFAULT_BUFFER_SIZE = 1000;

  protected final Map<Object, Integer>     fIndexMap           = new IdentityHashMap<Object, Integer>();
  protected final Deque<Object>            fObjects            = new ArrayDeque<Object>();
  protected final ClassNameRepositoryOut   fClassRepository    = new ClassNameRepositoryOut();
  protected final IObjectSerializerFactory fSerializerFactory;
  protected final ByteBufferWriter         fBuffer;


  public SerializationContext(IObjectSerializerFactory serializerFactory, Object rootObject) {
    this(serializerFactory, rootObject, DEFAULT_BUFFER_SIZE);
  }

  public SerializationContext(IObjectSerializerFactory serializerFactory, Object rootObject, int bufferSize) {
    fSerializerFactory = serializerFactory;
    fBuffer = new ByteBufferWriter(bufferSize);
    fObjects.add(rootObject);
    fIndexMap.put(rootObject, 1);
  }

  /**
   * Performs the serialization.
   *
   * @return an array of byte buffers containing the data
   */
  @SuppressWarnings("unchecked")
  public ByteBuffer[] serializeObjectGraph() {
    while (!fObjects.isEmpty()) {

      Object object = fObjects.pollFirst();
      Class<?> clazz = object.getClass();
      int classId = getIdForClass(clazz);
      IOUtil.writeIntCompact(fBuffer, classId);

      // Unchecked cast from <?> to <Object>:
      // We assume that the factory returns a IObjectSerializer<T>
      // where T is equal to object's concrete type!
      IObjectSerializer<Object> serializer = (IObjectSerializer<Object>) fSerializerFactory.getSerializer(clazz);

      serializer.serialize(this, object);
    }

    ByteBuffer[] result = new ByteBuffer[2];
    result[0] = writeHeader();
    result[1] = fBuffer.getByteBuffer();

    result[0].flip();
    result[1].flip();

    return result;
  }

  protected int getObjectReferenceId(@CheckForNull Object object) {
    if (object == null) {
      return 0;
    }
    Integer position = fIndexMap.get(object);
    if (position == null) {
      fObjects.add(object);
      position = fIndexMap.size() + 1;
      fIndexMap.put(object, position);
    }
    return position;
  }

  /**
   * Writes a header which is to be prepended to the serialized data.
   * <p>
   * Stores:
   * <ul>
   * <li>String pool.</li>
   * <li>Class info.</li>
   * <li>Total number of objects contained in the serialized data.</li>
   * </ul>
   */
  protected ByteBuffer writeHeader() {
    ByteBufferWriter header = new ByteBufferWriter();
    fClassRepository.serialize(header);
    IOUtil.writeIntCompact(header, fIndexMap.size());
    return header.getByteBuffer();
  }

  /**
   * Writes the given reference to the given writer.
   */
  protected void writeId(ByteBufferWriter writer, int id) {
    IOUtil.writeIntCompact(writer, id);
  }

  // IObjectSerializationContext

  public ByteBufferWriter getWriter() {
    return fBuffer;
  }

  public void writeObject(@CheckForNull Object object) {
    writeObject(fBuffer, object);
  }

  public int getIdForClass(Class<?> clazz) {
    return fClassRepository.getIdForClass(clazz);
  }

  // IObjectWriter

  public void writeObject(ByteBufferWriter writer, @CheckForNull Object object) {
    int id = getObjectReferenceId(object);
    writeId(writer, id);
  }

}