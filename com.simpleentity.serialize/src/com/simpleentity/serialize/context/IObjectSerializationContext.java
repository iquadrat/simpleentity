/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.context;

import com.simpleentity.util.io.ByteBufferWriter;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * Context for serializing object to a sequence of bytes.
 *
 * @author micha
 */
public interface IObjectSerializationContext extends IObjectWriter {

  /**
   * Writes a reference to the given object to the output. If the given
   * object has not been serialized itself, it will be scheduled to be serialized.
   */
  public void writeObject(@CheckForNull Object object);

  /**
   * @return the writer which can be used to serialize primitive types
   */
  public ByteBufferWriter getWriter();

  public int getIdForClass(Class<?> clazz);
}
