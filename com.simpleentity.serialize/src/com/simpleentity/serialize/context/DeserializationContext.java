/*
 * Created on Dec 1, 2007
 *
 */
package com.simpleentity.serialize.context;

import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.util.*;


import com.simpleentity.serialize.SerializationError;
import com.simpleentity.serialize.name.ClassNameRepositoryIn;
import com.simpleentity.serialize.object.*;
import com.simpleentity.util.Assert;
import com.simpleentity.util.io.*;

import edu.umd.cs.findbugs.annotations.Nullable;

public class DeserializationContext implements IObjectDeserializationContext {

  private interface IUnresolvedReference {
    void resolve(List<Object> objectList) throws IllegalArgumentException, IllegalAccessException;
  }

  protected final List<Object>             fObjects                 = new ArrayList<Object>();
  protected final IObjectSerializerFactory fSerializerFactory;

  @Nullable
  private List<IUnresolvedReference>       fUnresolvedReferences    = new ArrayList<IUnresolvedReference>();

  @Nullable
  private Deque<IPostDeserializationJob>   fPostDeserializationJobs = new LinkedList<IPostDeserializationJob>();

  @Nullable
  private ByteBufferReader                 fReader;

  private ClassNameRepositoryIn            fClassNameRepository;

  public DeserializationContext(IObjectSerializerFactory serializerFactory, ByteBuffer bytes) {
    fSerializerFactory = serializerFactory;
    fReader = new ByteBufferReader(bytes);
    fClassNameRepository = ClassNameRepositoryIn.deserialize(fReader);

    // the null reference is always the object with id 0, so put it first!
    fObjects.add(null);
  }

  /**
   * Deserializes the root object with all its references.
   *
   * @return the deserialized root object
   */
  public Object deserializeObjectGraph() {
    int objectCount = IOUtil.readIntCompact(fReader);
    for (int i = 0; i < objectCount; ++i) {
      Object object = deserializeObject();
      fObjects.add(object);
    }
    finishDeserialization();
    return fObjects.get(1);
  }

  // IDeserializationContext

  public final ByteBufferReader getReader() {
    return fReader;
  }

  public void addPostDeserializationJob(IPostDeserializationJob job) {
    fPostDeserializationJobs.addFirst(job);
  }

  public void readArrayReference(Object array, int index) {
    int id = readId(fReader);
    resolveArrayReference(array, index, id);
  }

  public void readFieldReference(Field field, Object object) throws IllegalArgumentException, IllegalAccessException {
    int id = readId(fReader);
    resolveFieldReference(field, object, id);
  }

  public Class<?> getClassForId(int id) {
    return fClassNameRepository.getClassForId(id);
  }

  // protected methods

  /**
   * Finishes deserialization by performing the outstanding tasks:
   * <ul>
   * <li>Resolve and set all unresolved references.</li>
   * <li>Execute the post-deserialization jobs.</li>
   * </ul>
   */
  protected void finishDeserialization() {
    try {
      for (IUnresolvedReference unboundReference: fUnresolvedReferences) {
        unboundReference.resolve(fObjects);
      }
    } catch (IllegalArgumentException e) {
      Assert.fail(e);
    } catch (IllegalAccessException e) {
      Assert.fail(e);
    }

    for (IPostDeserializationJob job: fPostDeserializationJobs) {
      job.execute(new IObjectReader() {
        public Object readObject(ByteBufferReader reader) {
          int id = readId(reader);
          return DeserializationContext.this.readObject(reader, id);
        }
      });
    }

    // free some memory
    fUnresolvedReferences = null;
    fPostDeserializationJobs = null;
    fReader = null;
    fClassNameRepository = null;
  }

  /**
   * Reads an object reference from the given stream.
   */
  protected int readId(ByteBufferReader reader) {
    return IOUtil.readIntCompact(reader);
  }

  protected void resolveArrayReference(final Object array, final int index, final int id) {
    if (id < fObjects.size()) {
      Array.set(array, index, fObjects.get(id));
    }
    fUnresolvedReferences.add(new IUnresolvedReference() {
      public void resolve(List<Object> objectList) {
        Array.set(array, index, objectList.get(id));
      }
    });
  }

  protected void resolveFieldReference(final Field field, final Object object, final int id) throws IllegalArgumentException, IllegalAccessException {
    if (id < fObjects.size()) {
      field.set(object, fObjects.get(id));
      return;
    }
    fUnresolvedReferences.add(new IUnresolvedReference() {
      public void resolve(List<Object> objectList) throws IllegalArgumentException, IllegalAccessException {
        field.set(object, objectList.get(id));
      }
    });
  }

  protected Object readObject(ByteBufferReader reader, int id) throws SerializationError {
    if (id >= fObjects.size()) {
      throw new SerializationError("Unkown object reference id: " + id);
    }
    return fObjects.get(id);
  }

  /**
   * Deserializes the next single object from the date.
   */
  protected Object deserializeObject() {
    int classId = IOUtil.readIntCompact(fReader);
    Class<?> clazz = getClassForId(classId);
    IObjectSerializer<?> serializer = fSerializerFactory.getSerializer(clazz);
    return serializer.deserialize(this);
  }

}