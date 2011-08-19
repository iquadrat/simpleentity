/*
 * Created on 05.07.2007
 *
 */
package com.simpleentity.util.disposable;

import com.simpleentity.util.Assert;

/**
 * NOTE: This class is not thread-safe!
 */
public abstract class AbstractDisposable implements IDisposable {

  private boolean fDisposed = false;

  @Override
  public final void dispose() {
    Assert.isFalse(fDisposed, "Double dispose detected on " + this);
    fDisposed = true;
    cleanup();
  }

  public final boolean isDisposed() {
    return fDisposed;
  }

  /**
   * This method is called when the object is disposed. It is guaranteed to be
   * called only once. Default is to do nothing.
   */
  protected void cleanup() {
  }

}
