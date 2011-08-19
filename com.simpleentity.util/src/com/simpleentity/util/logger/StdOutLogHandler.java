package com.simpleentity.util.logger;

import com.simpleentity.util.AssertionFailedError;

/**
 * Implementation that writes messages to standard output and
 * standard error.
 */
public class StdOutLogHandler implements ILogHandler {
  
  public StdOutLogHandler() {}
  
  @Override
  public void fail(AssertionFailedError failure) {
    throw failure;
  }
  
  @Override
  public void logError(String message) {
    System.err.println(message);
  }
  
  @Override
  public void logError(Throwable t) {
    t.printStackTrace(System.err);
  }
  
  @Override
  public void logInfo(Throwable t) {
    t.printStackTrace(System.out);
  }
  
  @Override
  public void logInfo(String message) {
    System.out.println(message);
  }
  
}