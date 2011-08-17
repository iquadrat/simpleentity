/*
 * Created on Dec 1, 2007
 *
 */
package com.simpleentity.serialize.context;


/**
 * Interface for jobs which are to be executed after the basic deserialization.
 * 
 * @see IObjectDeserializationContext#addPostDeserializationJob(IPostDeserializationJob)
 * 
 * @author micha
 *
 */
public interface IPostDeserializationJob {
  
  public void execute(IObjectReader objectReader);

}
