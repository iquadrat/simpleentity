/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.context;

import com.simpleentity.util.io.ByteBufferWriter;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * Interface for object reference writer.
 * 
 * @see IObjectReader
 * 
 * @author micha
 */
public interface IObjectWriter {

  /**
   * Writes a reference to the given object to the given stream. 
   * If the given object has not been serialized itself, it will be 
   * scheduled to be serialized.
   */
  public void writeObject(ByteBufferWriter stream, @CheckForNull Object object);

}
