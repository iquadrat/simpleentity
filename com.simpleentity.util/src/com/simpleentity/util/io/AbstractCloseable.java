/*
 * Created on 05.07.2007
 *
 */
package com.simpleentity.util.io;

import java.io.Closeable;
import java.io.IOException;

import edu.umd.cs.findbugs.annotations.OverrideMustInvoke;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public abstract class AbstractCloseable implements Closeable {

  private boolean fOpen;

  protected AbstractCloseable() {
    this(true);
  }

  protected AbstractCloseable(boolean initalOpen) {
    fOpen = initalOpen;
  }

  @Override
  public final void close() throws IOException {
    if (!fOpen) return;
    fOpen = false;
    internalClose();
  }

  public final boolean isOpen() {
    return fOpen;
  }

  protected void setOpen() {
    fOpen = true;
  }

  /**
   * This method is called when the object is closed. It is guaranteed to be called only once. Default is to do nothing.
   */
  @OverrideMustInvoke
  protected void internalClose() throws IOException {}

}
