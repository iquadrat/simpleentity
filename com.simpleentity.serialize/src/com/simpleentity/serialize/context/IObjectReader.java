/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.context;

import com.simpleentity.util.io.ByteBufferReader;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * Interface for object reference reader. 
 * 
 * @author micha
 */
public interface IObjectReader {
  
  /**
   * Reads an object reference from the given stream and looks up
   * the referred object.
   * 
   * @return the referred object
   */
  @CheckForNull
  public Object readObject(ByteBufferReader stream);

}
