/*
 * Created on Jan 27, 2008
 *
 */
package com.simpleentity.util.job;

public interface IJob<T> {

  public T resultRun();

  public IJobGroup getGroup();

}
