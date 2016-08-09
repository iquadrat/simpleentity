/*
 * Created on Oct 4, 2007
 *
 */
package com.simpleentity.util.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.simpleentity.util.AssertionFailedError;
import com.simpleentity.util.disposable.AbstractDisposable;
import com.simpleentity.util.logger.LogHandler;

public final class AbstractDisposableTest {

	private AbstractDisposable disposable;
	private boolean cleanup = false;

	@Before
	public void setUp() throws Exception {
		disposable = new AbstractDisposable() {
			@Override
			protected void cleanup() {
				super.cleanup();
				cleanup = true;
			}
		};
	}

	@Test
	public void dispose() {
		assertFalse(disposable.isDisposed());
		assertFalse(cleanup);
		disposable.dispose();
		assertTrue(cleanup);
		assertTrue(disposable.isDisposed());
	}

	@Test
	public void doubleDispose() {
		com.simpleentity.util.logger.Logger.getRootLogger().setLogHandler(new LogHandler() {
			@Override
			public void handle(LogLevel level, String message, Throwable throwable) {}
		});

		disposable.dispose();
		assertTrue(cleanup);
		cleanup = false;

		try {
			disposable.dispose();
			fail("exception expected");
		} catch (AssertionFailedError e) {
			// pass
		}
		assertTrue(disposable.isDisposed());
		assertFalse(cleanup);
	}
}
