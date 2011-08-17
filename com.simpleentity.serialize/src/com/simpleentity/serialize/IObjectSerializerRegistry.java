/*
 * Created on Dec 4, 2007
 *
 */
package com.simpleentity.serialize;

import com.simpleentity.serialize.object.IObjectSerializer;

public interface IObjectSerializerRegistry {
  
  /**
   * Registers a custom serializer for the given class.
   */
  public <T> void registerSerializer(Class<T> clazz, IObjectSerializer<T> serializer);

}
