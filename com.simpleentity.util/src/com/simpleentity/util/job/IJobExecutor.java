/*
 * Created on Jan 27, 2008
 *
 */
package com.simpleentity.util.job;

import com.simpleentity.util.disposable.IDisposable;

public interface IJobExecutor {

  public void execute(IJobGroup jobGroup);

  public IDisposable addListener(IJobGroup group, IJobExecutionListener listener);

  public void waitExecutionDone(IJobGroup jobGroup) throws InterruptedException;

}
