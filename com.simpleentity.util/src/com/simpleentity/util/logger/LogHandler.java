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

import edu.umd.cs.findbugs.annotations.CheckForNull;

public interface LogHandler {

	public enum LogLevel {
		FATAL, 
		ERROR,
		WARNING,
		INFO,
		VERBOSE,
	}
	
	public void handle(LogLevel level, @CheckForNull String message, @CheckForNull Throwable throwable);
}
