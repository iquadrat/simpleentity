/*
 * Created on Jan 27, 2008
 *
 */
package com.simpleentity.util.job;

public interface IJobExecutionListener {

  public void jobDone(IJob<?> job, Object result);

}
