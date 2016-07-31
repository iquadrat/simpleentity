/*
 * Created on 14.07.2007
 *
 */
package com.simpleentity.util.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public class IOUtil {

  public static final Charset CHARSET_UFT8 = Charset.forName("UTF-8");

  private IOUtil() {
    // utility class
  }

  /**
   * Writes an integer into an array of 4 bytes.
   *
   * @see #readInt(byte[])
   */
  public static void writeInt(byte[] bytes, int value) {
    bytes[0] = (byte) (value >> 24);
    bytes[1] = (byte) (value >> 16);
    bytes[2] = (byte) (value >> 8);
    bytes[3] = (byte) (value);
  }

//  public static void writeInt(OutputStream stream, int value) throws IOException {
//    stream.write((byte) (value >> 24));
//    stream.write((byte) (value >> 16));
//    stream.write((byte) (value >> 8));
//    stream.write((byte) (value));
//  }

  /**
   * Reads an integer previously written using {@link #writeInt(byte[], int)}
   * from an array of 4 bytes.
   */
  public static int readInt(byte[] bytes) {
    int value = (bytes[0] & 0xFF);
    value = (value << 8) | (bytes[1] & 0xFF);
    value = (value << 8) | (bytes[2] & 0xFF);
    value = (value << 8) | (bytes[3] & 0xFF);
    return value;
  }

//  public static int readInt(InputStream stream) throws IOException {
//    int value = (stream.read() & 0xFF);
//    value = (value << 8) | (stream.read() & 0xFF);
//    value = (value << 8) | (stream.read() & 0xFF);
//    value = (value << 8) | (stream.read() & 0xFF);
//    return value;
//  }

  /**
   * @see ByteBufferWriter#putCompactIntUnsigned(int)
   */
  public static void writeIntCompact(ByteBufferWriter writer, int value) {
	  writer.putCompactIntUnsigned(value);
  }


  /**
   * @see ByteBufferReader#readCompactIntUnsigned()
   */
  public static int readIntCompact(ByteBufferReader reader) {
	  return reader.readCompactIntUnsigned();
  }

  /**
   * Encodes the given positive long to bytes and writes it to the given writer.
   * Writes 1 to 9 bytes depending on the value.
   */
  public static void writeLongCompact(ByteBufferWriter writer, long value) {
    if (value < 0) {
      throw new IllegalArgumentException("Cannot write negative values: " + value);
    }
    
    long remaining = value;
    long next = (remaining >>> 7);
    while (next != 0) {
      writer.putByte((byte) (((byte) remaining) | 0x80));
      remaining = next;
      next >>>= 7;
    }
    writer.putByte((byte) remaining);
  }

  /**
   * Reads a positive long from the given reader which has been written using
   * {@link #writeLongCompact(ByteBufferWriter, long)}.
   */
  public static long readLongCompact(ByteBufferReader reader) {
    long result = 0;
    int value = reader.readUnsignedByte();
    long factor = 1;
    while (value > 127) {
      result += (value ^ 0x80) * factor;
      value = reader.readUnsignedByte();
      factor <<= 7;
    }
    return result + value * factor;
  }

  /**
   * Writes a string encoded in UTF8 to the given writer.
   */
  public static void writeStringUTF8(ByteBufferWriter writer, @CheckForNull String value) {
    if (value == null) {
      writer.write(0);
      return;
    }

    byte[] bytes = value.getBytes(CHARSET_UFT8);
    // add 1 to the length because 0 means null reference
    writeIntCompact(writer, bytes.length + 1);
    writer.write(bytes);
  }

  /**
   * Reads a string from the given reader which has been written using
   * {@link #readStringUTF8(ByteBufferReader)}.
   */
  @CheckForNull
  public static String readStringUTF8(ByteBufferReader reader) {
    int length = readIntCompact(reader);
    if (length == 0) {
      return null;
    }

    byte[] content = new byte[length - 1];
    reader.read(content);
    return new String(content, CHARSET_UFT8);
  }

  /**
   * @return the sum of remaining bytes in all the given buffers
   */
  public static int dataSize(ByteBuffer[] buffers) {
    int size = 0;
    for (ByteBuffer buffer: buffers) {
      size += buffer.remaining();
    }
    return size;
  }

  /**
   * Writes the remaining bytes of the given buffers to the given writer. Does
   * not work with directly allocated byte buffers!
   */
  public static void writeBuffers(ByteBufferWriter writer, ByteBuffer... buffers) {
    for (ByteBuffer buffer: buffers) {
      writer.write(buffer.array(), buffer.position(), buffer.remaining());
    }
  }

  public static byte[] compact(ByteBuffer[] buffers) {
    int size = 0;
    for (ByteBuffer buffer: buffers) {
      size += buffer.remaining();
    }
    byte[] bytes = new byte[size];

    int offset = 0;
    for (ByteBuffer buffer: buffers) {
      int l = buffer.remaining();
      buffer.get(bytes, offset, l);
      offset += l;
    }
    return bytes;
  }

  public static void closeSilently(@CheckForNull Closeable closeable) {
    if (closeable == null) {
      return;
    }
    try {
      closeable.close();
    } catch (IOException e) {
      // ignore
    }
  }

  /**
   * Guarantees to invoke {@link Closeable#close()} on all given closeables.
   * 
   * @throws IOException if any {@link Closeable} throws an {@link IOException} during close
   */
  public static void close(@CheckForNull Closeable... closeables) throws IOException {
    IOException ioException = null;
    for(Closeable closable: closeables) {
      if (closable == null) continue;
      try {
        closable.close();
      } catch(IOException e) {
        if (ioException != null) {
          ioException = e; // TODO add multi-io-exception?
        }
      }
    }
    if (ioException != null) {
      throw ioException;
    }
  }

}
