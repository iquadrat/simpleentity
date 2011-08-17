/*
 * Created on Jan 27, 2008
 *
 */
package com.simpleentity.util.job;

import com.simpleentity.util.collection.IListenerHandle;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public interface IJobGroup {

  @CheckForNull
  public IJob<?> getNextJob();

  public IListenerHandle addJobAvailbilityListener(IJobAvailabilityListener listener);

}
