/*
 * Created on Dec 8, 2007
 *
 */
package org.povworld.serialize.name;

import junit.framework.TestCase;


import com.simpleentity.serialize.name.*;
import com.simpleentity.util.io.*;

public class StringPoolTest extends TestCase {

  public void test_write_and_read() {
    StringPoolOut out = new StringPoolOut();
    assertEquals(0, out.getId("foo"));
    assertEquals(1, out.getId("bar"));
    assertEquals(0, out.getId("foo"));
    assertEquals(2, out.getId("Hello"));
    assertEquals(3, out.getId("world!"));

    ByteBufferWriter writer = new ByteBufferWriter();
    out.serialize(writer);
    writer.getByteBuffer().flip();
    ByteBufferReader reader = new ByteBufferReader(writer.getByteBuffer());

    StringPoolIn in = StringPoolIn.deserialize(reader);
    assertEquals("foo", in.getString(0));
    assertEquals("bar", in.getString(1));
    assertEquals("Hello", in.getString(2));
    assertEquals("world!", in.getString(3));
  }

}
