/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.context;

import java.lang.reflect.Field;

import com.simpleentity.util.io.ByteBufferReader;

/**
 * Context for deserializing object from a sequence of bytes.
 *
 * @author micha
 */
public interface IObjectDeserializationContext {

  /**
   * @return the reader for the stored data which can be used to read primitive types
   */
  public ByteBufferReader getReader();

  /**
   * Tells the object reference handler to read an object reference from the
   * stored data and set the given field of the given object to referred object.
   * Note that it is up to the deserializer if this reference is set immediately
   * on calling the method or later on during serialization. In any case it
   * is guaranteed to have been set before the post-deserialization jobs are run.
   */
  public void readFieldReference(Field field, Object object) throws IllegalArgumentException, IllegalAccessException;

  /**
   * Tells the object reference handler to read an object reference from the
   * stored data and set the given index of the given array to referred object.
   * Note that it is up to the deserializer if this reference is set immediately
   * on calling the method or later on during serialization. In any case it
   * is guaranteed to have been set before the post-deserialization jobs are run.
   *
   * @param array must be a single-dimensioned array!
   */
  public void readArrayReference(Object array, int index);

  /**
   * Adds a job which is invoked at the end of the deserialization process.
   * At that time instances of all objects are alive and the field references
   * and array references which were requested through
   * <ul>
   * <li>{@link #readFieldReference(Field, Object)} and</li>
   * <li>{@link #readArrayReference(Object, int)}</li>
   * </ul>
   * are set.
   * <p>
   * The jobs are invoked in reverse order of adding.
   */
  public void addPostDeserializationJob(IPostDeserializationJob job);

  public Class<?> getClassForId(int id);

}
