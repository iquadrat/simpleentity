/*
 * Created on Oct 4, 2007
 *
 */
package com.simpleentity.util.test;

import junit.framework.TestCase;

import com.simpleentity.util.AssertionFailedError;
import com.simpleentity.util.disposable.AbstractDisposable;

public class AbstractDisposableTest extends TestCase {

  private AbstractDisposable fDisposable;
  protected boolean          fCleanup;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    fCleanup = false;
    fDisposable = new AbstractDisposable() {
      @Override
      protected void cleanup() {
        super.cleanup();
        fCleanup = true;
      }
    };
  }

  public void test_dispose() {
    assertFalse(fDisposable.isDisposed());
    assertFalse(fCleanup);
    fDisposable.dispose();
    assertTrue(fCleanup);
    assertTrue(fDisposable.isDisposed());
  }

  public void test_double_dispose() {
    fDisposable.dispose();
    assertTrue(fCleanup);
    fCleanup = false;

    try {
      fDisposable.dispose();
      fail("exception expected");
    } catch (AssertionFailedError e) {
      // pass
    }
    assertTrue(fDisposable.isDisposed());
    assertFalse(fCleanup);

  }

}
