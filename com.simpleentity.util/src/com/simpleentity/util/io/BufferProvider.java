/*
 * Created on 27.11.2007
 *
 */
package com.simpleentity.util.io;

import java.lang.ref.WeakReference;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * This class is to be used by a single thread.
 * 
 * @author micha
 */
public class BufferProvider {

  private static final int                      OUTPUT_BUFFER_SIZE = 10000;

  @CheckForNull
  private WeakReference<ByteBufferWriter> fOutputBuffer      = null;

  public ByteBufferWriter getBuffer() {
    ByteBufferWriter result = null;
    if (fOutputBuffer != null) {
      result = fOutputBuffer.get();
    }
    if (result == null) {
      result = new ByteBufferWriter(OUTPUT_BUFFER_SIZE);
      fOutputBuffer = new WeakReference<ByteBufferWriter>(result);
    }
    result.clear();
    return result;
  }

}
