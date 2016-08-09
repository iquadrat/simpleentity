/*
 * Created on 02.05.2010
 *
 */
package com.simpleentity.util.io;

import java.io.IOException;

import net.jcip.annotations.NotThreadSafe;

import com.simpleentity.util.collection.ListenerHandle;
import com.simpleentity.util.collection.ListenerList;

import edu.umd.cs.findbugs.annotations.OverrideMustInvoke;

@NotThreadSafe
public abstract class AbstractObservableCloseable extends AbstractCloseable {

  private final ListenerList<ICloseListener> fCloseListeners = new ListenerList<ICloseListener>();

  public ListenerHandle addCloseListener(ICloseListener listener) {
    return fCloseListeners.add(listener);
  }

  @Override
  @OverrideMustInvoke
  protected void internalClose() throws IOException {
    super.internalClose();
    for(ICloseListener closeListener: fCloseListeners) {
      closeListener.notifyClosed();
    }
  }

}
