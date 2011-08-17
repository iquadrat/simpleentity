/*
 * Created on 04.07.2007
 *
 */
package com.simpleentity.util;

public class AssertionFailedError extends Error {

  private static final long serialVersionUID = 1L;
  
  public AssertionFailedError(String message) {
    super(message);
  }

  public AssertionFailedError(Throwable t) {
    super(t);
  }

}
