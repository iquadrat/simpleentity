/*
 * Created on Jan 27, 2008
 *
 */
package com.simpleentity.util.job;

public abstract class AbstractJob implements IJob<Void> {

  private final IJobGroup fGroup;

  public AbstractJob(IJobGroup group) {
    fGroup = group;
  }

  @Override
  public final Void resultRun() {
    run();
    return null;
  }

  @Override
  public final IJobGroup getGroup() {
    return fGroup;
  }

  public abstract void run();

}
