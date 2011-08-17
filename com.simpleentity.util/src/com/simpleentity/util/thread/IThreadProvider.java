/*
 * Created on Jan 28, 2008
 *
 */
package com.simpleentity.util.thread;

public interface IThreadProvider {

  /**
   * Must be thread safe!
   */
  public IThreadExecutor getThreadExecutor();

  /**
   * Must be thread safe!
   */
  public void release(IThreadExecutor executor);

}
