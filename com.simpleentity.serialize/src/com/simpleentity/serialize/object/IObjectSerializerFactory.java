/*
 * Created on Dec 1, 2007
 *
 */
package com.simpleentity.serialize.object;

/**
 * Factory which is able to create object serializers for any class.
 *
 * @author micha
 */
public interface IObjectSerializerFactory {

  /**
   * Returns a serializer which is able to serialize objects who's concrete type
   * is equal to the given class.
   */
  public IObjectSerializer<?> getSerializer(Class<?> clazz);

//  /**
//   * Convenience method to be used instead of <code>getSerializer(Class.forName(className))</code>.
//   */
//  public IObjectSerializer<?> getSerializer(String className) throws ClassNotFoundException;
//
//  /**
//   * Convenience method to be used instead of <code>getSerializer(object.getClass())</code>.
//   */
//  public IObjectSerializer<?> getSerializer(Object object);

}