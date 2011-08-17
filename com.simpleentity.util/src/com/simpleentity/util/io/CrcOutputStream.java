/*
 * Created on 02.05.2010
 *
 */
package com.simpleentity.util.io;

import java.io.*;
import java.util.zip.CRC32;

public class CrcOutputStream extends OutputStream {

  private final CRC32        fCrc = new CRC32();
  private final OutputStream fDelegate;
  private int fBytesWritten = 0;

  public CrcOutputStream(OutputStream delegate) {
    fDelegate = delegate;
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    if (b == null) {
      throw new NullPointerException();
    } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
      throw new IndexOutOfBoundsException();
    }
    fDelegate.write(b, off, len);
    fCrc.update(b, off, len);
    fBytesWritten += len;
  }

  @Override
  public void write(int b) throws IOException {
    fCrc.update(b);
    fDelegate.write(b);
    fBytesWritten++;
  }

  public int getCrc() {
    return (int) fCrc.getValue();
  }
  
  public int getBytesWritten() {
    return fBytesWritten;
  }

}
