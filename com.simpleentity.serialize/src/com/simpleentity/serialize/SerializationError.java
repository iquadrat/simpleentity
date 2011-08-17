/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize;

/**
 * Error which is thrown on any error which is encountered during
 * serialization or deserialization.
 * 
 * @author micha
 */
public class SerializationError extends Error {

  private static final long serialVersionUID = 1L;
  
  public SerializationError(Throwable t) {
    super(t);
  }

  public SerializationError(String string) {
    super(string);
  }

}
