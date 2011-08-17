/*
 * Created on Jan 28, 2008
 *
 */
package com.simpleentity.util.thread;

import com.simpleentity.util.disposable.IDisposable;


public interface IThreadExecutor extends IDisposable {

  public void execute(IRunnable runnable);

}
