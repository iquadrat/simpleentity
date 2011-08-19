/*
 * Created on Dec 2, 2007
 *
 */
package org.povworld.serialize;

import java.nio.ByteBuffer;


import com.simpleentity.serialize.ObjectGraphSerializer;
import com.simpleentity.util.io.IOUtil;

import junit.framework.TestCase;

public abstract class AbstractSerializationTest extends TestCase {
  
  protected ObjectGraphSerializer fSerializer;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    fSerializer = new ObjectGraphSerializer();
  }

  @Override
  protected void tearDown() throws Exception {
    fSerializer = null;
    super.tearDown();
  }
  
  @SuppressWarnings("unchecked")
  protected <T> T serializeAndDeserialize(T p) {
    ByteBuffer[] data = fSerializer.serializeObjectGraph(p);
    ByteBuffer reader = createInput(data);
    T actual = (T) fSerializer.deserializeObjectGraph(reader);
    return actual;
  }
  
  protected ByteBuffer createInput(ByteBuffer[] data) {
    return ByteBuffer.wrap(IOUtil.compact(data));
  }

}
