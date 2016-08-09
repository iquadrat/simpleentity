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

import com.simpleentity.util.AssertionFailedError;


public interface LogHandler {
  
  /**
   * Is called when a failure occurred.
   * Must throw an {@link AssertionFailedError}!
   * 
   * @param failure failure that occurred
   */
  public void fail(AssertionFailedError failure);
  
  /**
   * Logs the given message as error without throwing an exception.
   * 
   * @param message message to be logged
   */
  public void logError(String message);
  
  /**
   * Logs the given {@link Throwable} as error without throwing an exception.
   */
  public void logError(Throwable throwable);

  /**
   * Logs the given message as info without throwing an exception.
   * 
   * @param message message to be logged
   */
  public void logInfo(String message);
  
  /**
   * Logs the given {@link Throwable} as info without throwing an exception.
   */
  public void logInfo(Throwable t);
  
}
