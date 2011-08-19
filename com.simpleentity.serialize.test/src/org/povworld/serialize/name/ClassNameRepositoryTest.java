/*
 * Created on Dec 8, 2007
 *
 */
package org.povworld.serialize.name;

import junit.framework.TestCase;


import com.simpleentity.serialize.name.*;
import com.simpleentity.util.io.*;

public class ClassNameRepositoryTest extends TestCase {

  private ClassNameRepositoryOut fRepoOut;
  private ClassNameRepositoryIn  fRepoIn;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    fRepoOut = new ClassNameRepositoryOut();
  }

  public void test_single_class() {
    int id = fRepoOut.getIdForClass(ClassNameRepositoryTest.class);
    writeAndRead();
    assertEquals(ClassNameRepositoryTest.class, fRepoIn.getClassForId(id));
  }

  private void writeAndRead() {
    ByteBufferWriter writer = new ByteBufferWriter();
    fRepoOut.serialize(writer);
    writer.getByteBuffer().flip();
    ByteBufferReader reader = new ByteBufferReader(writer.getByteBuffer());
    fRepoIn = ClassNameRepositoryIn.deserialize(reader);
  }

}
