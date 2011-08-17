/*
 * Created on Jan 28, 2008
 *
 */
package com.simpleentity.util.thread;

import java.util.concurrent.locks.*;

import com.simpleentity.util.Assert;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public class ThreadExecutor extends Thread implements IThreadExecutor {

  private final Lock      fLock;

  private final Condition fRunnableAvailable;

  private boolean         fDisposed = false;

  @CheckForNull
  private IRunnable       fRunnable = null;

  public ThreadExecutor() {
    fLock = new ReentrantLock();
    fRunnableAvailable = fLock.newCondition();
  }

  @Override
  public void execute(IRunnable runnable) {
    fLock.lock();
    try {
      if (fRunnable != null) {
        Assert.fail("ThreadExecutor is already running!");
      }
      fRunnable = runnable;
      fRunnableAvailable.signal();
    } finally {
      fLock.unlock();
    }
  }

  @Override
  public void run() {
    for (;;) {
      fLock.lock();
      try {
        while(fRunnable == null) {
          if (fDisposed) {
            return;
          }
          fRunnableAvailable.await();
        }
        fRunnable.run();
        fRunnable = null;
      } catch (InterruptedException e) {
        Assert.isTrue(fDisposed, "Thread interrupted but not disposed!");
      } finally {
        fLock.unlock();
      }
    }
  }

  @Override
  public void dispose() {
    fLock.lock();
    try {
      if (fDisposed) {
        Assert.fail("Double dispose detected!");
      }
      fDisposed = true;
      this.interrupt();
    } finally {
      fLock.unlock();
    }
  }

}
