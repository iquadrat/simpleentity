/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.object;

import com.simpleentity.serialize.context.*;

/**
 * Interface for entries in the serialization of the {@link GenericObjectSerializer}.
 *
 * @author micha
 */
public interface ISerializationEntry {

  /**
   * Write this serialization entry.
   */
  public void write(IObjectSerializationContext context, Object object) throws IllegalAccessException;

  /**
   * Read this serialization entry.
   */
  public void read(IObjectDeserializationContext context, Object result) throws IllegalAccessException;

}
