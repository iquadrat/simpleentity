/*
 * Created on Jan 28, 2008
 *
 */
package com.simpleentity.util.job;

import java.util.*;

import com.simpleentity.util.collection.*;

public class DequeJobGroup<T extends IJob<?>> implements IJobGroup {

  private final Deque<T> fJobs = new ArrayDeque<T>();

  private final ListenerList<IJobAvailabilityListener> fListeners = new ListenerList<IJobAvailabilityListener>();

  public DequeJobGroup(T... jobs) {
    fJobs.addAll(Arrays.asList(jobs));
  }

  public synchronized void add(T job) {
    fJobs.add(job);
    for(IJobAvailabilityListener listener: fListeners) {
      listener.notifyNewJobAvailable();
    }
  }

  @Override
  public synchronized IListenerHandle addJobAvailbilityListener(IJobAvailabilityListener listener) {
    return fListeners.add(listener);
  }

  @Override
  public synchronized T getNextJob() {
    if (fJobs.isEmpty()) {
      return null;
    }
    return fJobs.removeFirst();
  }

}
