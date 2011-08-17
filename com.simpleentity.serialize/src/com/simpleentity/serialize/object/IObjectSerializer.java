/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.object;

import com.simpleentity.serialize.context.*;

/**
 * Serializes objects of some concrete type <code>T</code>.
 * 
 * @author micha
 */
public interface IObjectSerializer<T> {

  /**
   * Serializes the given object to the output stream.
   */
  public void serialize(IObjectSerializationContext context, T object);

  /**
   * Deserializes an object from the input stream.
   */
  public T deserialize(IObjectDeserializationContext context);

}
