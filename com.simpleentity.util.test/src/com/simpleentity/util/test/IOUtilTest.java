/*
 * Created on 14.07.2007
 *
 */
package com.simpleentity.util.test;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

import com.simpleentity.util.io.*;

public class IOUtilTest extends TestCase {
  
  private ByteBufferWriter fWrite;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    fWrite = new ByteBufferWriter();
  }
  
  @Override
  protected void tearDown() throws Exception {
    fWrite = null;
    super.tearDown();
  }

  public void test_read_and_write_ints_fixed_size() {
    readAndWriteInt(0);
    readAndWriteInt(1);
    readAndWriteInt(-1);
    readAndWriteInt(17);
    readAndWriteInt(-129);
    readAndWriteInt(28312834);
    readAndWriteInt(-1437591932);
    readAndWriteInt(1239399991);
    readAndWriteInt(4772811);
    readAndWriteInt(-1237548581);
  }
  
  private static void readAndWriteInt(int i) {
    byte[] bytes = new byte[4];
    IOUtil.writeInt(bytes, i);
    assertEquals(i, IOUtil.readInt(bytes));
  }

  
  public void test_int_low_values() {
    for(int i=0; i<100000; ++i) {
      checkInt(i);
    }
  }
  
  public void test_int_high_values() {
    checkInt(456789);
    checkInt(2000001);
    checkInt(2097150);
    checkInt(2097151);
    checkInt(2097152);
    checkInt(2097153);
    checkInt(2097154);
    checkInt(123451232);
    checkInt(268435454);
    checkInt(268435455);
    checkInt(268435456);
    checkInt(268435457);
    checkInt(319231111);
    checkInt(536870910);
    checkInt(536870911);
    checkInt(536870912);
    checkInt(536870913);
    checkInt(Integer.MAX_VALUE);
  }

  private void checkInt(int i) {
    fWrite.clear();
    IOUtil.writeIntCompact(fWrite, i);
    
    ByteBufferReader in = new ByteBufferReader(getBufferForReading(fWrite));
    int j = IOUtil.readIntCompact(in);
    
    assertEquals(i,j);
  }
  
  public void test_long_low_values() {
    for(long i=0; i<100000; ++i) {
      checkLong(i);
    }
  }
  
  public void test_long_high_values() {
    checkLong(456789L);
    checkLong(2000001L);
    checkLong(2097150L);
    checkLong(2097151L);
    checkLong(2097152L);
    checkLong(2097153L);
    checkLong(2097154L);
    checkLong(123451232L);
    checkLong(268435454L);
    checkLong(268435455L);
    checkLong(268435456L);
    checkLong(268435457L);
    checkLong(319231111L);
    checkLong(536870910L);
    checkLong(536870911L);
    checkLong(536870912L);
    checkLong(536870913L);
    checkLong(112536870913L);
    checkLong(98112536870913L);
    checkLong(17698112536870913L);
    checkLong(Long.MAX_VALUE);
  }

  private void checkLong(long i) {
    fWrite.clear();
    IOUtil.writeLongCompact(fWrite, i);
    
    ByteBufferReader in = new ByteBufferReader(getBufferForReading(fWrite));
    long j = IOUtil.readLongCompact(in);
    
    assertEquals(i,j);
  }
  
  public void test_write_null_string() {
    testString(null);
  }
  
  public void test_write_zero_length_string() {
    testString("");
  }
  
  public void test_write_strings() {
    testString("Hello world!!");
    testString("43");
    testString("häui");
  }
  
  private void testString(String s) {
    fWrite.clear();
    IOUtil.writeStringUTF8(fWrite, s);
    ByteBufferReader in = new ByteBufferReader(getBufferForReading(fWrite));
    String actual = IOUtil.readStringUTF8(in);
    assertEquals(actual, s);
  }

  private ByteBuffer getBufferForReading(ByteBufferWriter write) {
    ByteBuffer buffer = write.getByteBuffer();
    buffer.flip();
    return buffer;
  }

}
