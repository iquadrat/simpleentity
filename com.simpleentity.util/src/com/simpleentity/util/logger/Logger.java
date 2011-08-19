/*******************************************************************************
 * Copyright (c) 2010 actifsource GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     actifsource GmbH - initial API and implementation
 ******************************************************************************/
/*
 * Created on 20.09.2005
 *
 */
package com.simpleentity.util.logger;

import com.simpleentity.util.*;

/**
 * Default logging implementation.
 * 
 * @author micha
 */
public class Logger {
  
  private static Logger sLogger     = new Logger();
  
  private ILogHandler   fHandler = new StdOutLogHandler();
  
  public static Logger instance() {
    return sLogger;
  }
  
  /**
   * Logs the given <code>failure</code>.
   */
  public void fail(AssertionFailedError failure) {
    fHandler.fail(failure);
  }
  
  /**
   * Logs the given <code>message</code>.
   */
  public void logError(String message) {
    fHandler.logError(message);
  }
  
  /**
   * Logs the given <code>throwable</code>.
   */
  public void logError(Throwable throwable) {
    fHandler.logError(throwable);
  }
  
  public void logInfo(Throwable t) {
    fHandler.logInfo(t);
  }
  
  public void logInfo(String message) {
    fHandler.logInfo(message);
  }
  
  public void logVerbose(Throwable t) {
    fHandler.logInfo(t);
  }
  
  public void logVerbose(String message) {
    fHandler.logInfo(message);
  }
  
  /**
   * Replaces the log handler by the given <code>handler</code>.
   * @return the old handler
   */
  public ILogHandler setLogHandler(ILogHandler handler) {
    ILogHandler oldHandler = fHandler;
    fHandler = handler;
    return oldHandler;
  }
  
}
