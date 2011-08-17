/*
 * Created on Jan 28, 2008
 *
 */
package com.simpleentity.util.thread;

public class ThreadProvider implements IThreadProvider {

  @Override
  public IThreadExecutor getThreadExecutor() {
    ThreadExecutor result = new ThreadExecutor();
    result.start();
    return result;
  }

  @Override
  public void release(IThreadExecutor executor) {
    // TODO reuse
  }

}
