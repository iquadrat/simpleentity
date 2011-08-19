/*
 * Created on Dec 2, 2007
 *
 */
package org.povworld.serialize.testclasses;

import java.io.*;
import java.util.Arrays;

import junit.framework.Assert;

public class SerializeMe implements Serializable {
  
  private static final byte[] DATA = "this is my private data".getBytes();
  
  private static final long serialVersionUID = 1L;
  
  public boolean fReadCalled = false;
  public boolean fWriteCalled = false;

  private void readObject(ObjectInputStream in) throws IOException {
    fReadCalled = true;
    byte[] data = new byte[DATA.length];
    Assert.assertEquals(in.read(data), data.length);
    Assert.assertTrue(Arrays.equals(data, DATA));
  }
  
  private void writeObject(ObjectOutputStream out) throws IOException {
    fWriteCalled = true;
    out.write(DATA);
  }
  
}