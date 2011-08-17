/*
 * Created on Jan 27, 2008
 *
 */
package com.simpleentity.util.job;

import java.util.*;
import java.util.concurrent.locks.*;


import com.simpleentity.util.Assert;
import com.simpleentity.util.collection.*;
import com.simpleentity.util.disposable.IDisposable;
import com.simpleentity.util.thread.*;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public class MultiCoreJobExecutor implements IJobExecutor {

  private final OrderedSet<IJobGroup>                         fExecutableGroups   = new OrderedSet<IJobGroup>();
  private final Set<IJob<?>>                                  fRunningJobs        = new HashSet<IJob<?>>();
  private final ListenerMap<IJobGroup, IJobExecutionListener> fExecutionListeners = new ListenerMap<IJobGroup, IJobExecutionListener>();

  private final Lock                                          fLock;
  private final Condition                                     fJobDone;
  private final int                                           fParallelThreads;
  private final IThreadProvider                               fThreadProvider;

  private int                                                 fThreadsInUse       = 0;

  public MultiCoreJobExecutor() {
    fLock = new ReentrantLock();
    fThreadProvider = new ThreadProvider();
    fJobDone = fLock.newCondition();
    fParallelThreads = Runtime.getRuntime().availableProcessors();
  }

  @Override
  public IDisposable addListener(IJobGroup group, IJobExecutionListener listener) {
    fLock.lock();
    try {
      return fExecutionListeners.add(group, listener);
    } finally {
      fLock.unlock();
    }
  }

  @Override
  public void execute(final IJobGroup jobGroup) {
    fLock.lock();
    try {
      fExecutableGroups.add(jobGroup);
      jobGroup.addJobAvailbilityListener(new IJobAvailabilityListener() {

        @Override
        public void notifyNewJobAvailable() {
          makeJobGroupActive(jobGroup);
        }

      });
      executeNextJobs();
    } finally {
      fLock.unlock();
    }
  }

  protected void makeJobGroupActive(IJobGroup jobGroup) {
    fLock.lock();
    try {
      if (fExecutableGroups.contains(jobGroup)) {
        return;
      }
      fExecutableGroups.add(jobGroup);
      executeNextJobs();
    } finally {
      fLock.unlock();
    }
  }

  /**
   * NOTE: MUST OWN THE LOCK PRIOR TO ENTER THIS METHOD!
   */
  private void executeNextJobs() {
    for (;;) {
      if (fThreadsInUse == fParallelThreads) {
        // we're busy as possible
        return;
      }

      final IJob<?> job = getNextjob();
      if (job == null) {
        // hey, there's nothing to do any more
        return;
      }

      // set-up new executor
      fThreadsInUse++;
      final IThreadExecutor executor = fThreadProvider.getThreadExecutor();
      fRunningJobs.add(job);

      executor.execute(new IRunnable() {
        @Override
        public void run() {
          IJob<?> myJob = job;
          while (myJob != null) {
            Object result = myJob.resultRun();
            fLock.lock();
            try {
              fRunningJobs.remove(myJob);
              notifyJobDone(myJob, result);
              myJob = getNextjob();
              if (myJob != null) {
                fRunningJobs.add(myJob);
              }
            } finally {
              fLock.unlock();
            }
          }
          fThreadProvider.release(executor);
          fThreadsInUse--;
        }
      });
    }

  }

  /**
   * NOTE: MUST OWN THE LOCK PRIOR TO ENTER THIS METHOD!
   */
  protected void notifyJobDone(IJob<?> job, Object result) {
    fJobDone.signal();
    for (IJobExecutionListener listener: fExecutionListeners.getListeners(job.getGroup())) {
      listener.jobDone(job, result);
    }
  }

  /**
   * NOTE: MUST OWN THE LOCK PRIOR TO ENTER THIS METHOD!
   */
  @CheckForNull
  private IJob<?> getNextjob() {
    for (IJobGroup group: new ArrayList<IJobGroup>(fExecutableGroups.asUnmodifiableList())) {
      IJob<?> job = group.getNextJob();
      if (job != null) {
        return job;
      }
      fExecutableGroups.remove(group);
    }
    return null;
  }

  @Override
  public void waitExecutionDone(IJobGroup jobGroup) throws InterruptedException {
    Assert.isNotNull(jobGroup);
    fLock.lock();
    try {
      for (;;) {
        if (!fExecutableGroups.contains(jobGroup) && !isRunning(jobGroup)) {
          return;
        }
        fJobDone.await();
      }
    } finally {
      fLock.unlock();
    }
  }

  /**
   * NOTE: MUST OWN THE LOCK PRIOR TO ENTER THIS METHOD!
   */
  private boolean isRunning(IJobGroup jobGroup) {
    for (IJob<?> job: fRunningJobs) {
      if (jobGroup.equals(job.getGroup())) {
        return true;
      }
    }
    return false;
  }

  // TODO dispose method

}
