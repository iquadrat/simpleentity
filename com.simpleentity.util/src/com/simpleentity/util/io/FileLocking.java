/*
 * Created on 02.05.2010
 *
 */
package com.simpleentity.util.io;

import java.io.*;
import java.nio.channels.FileLock;


public class FileLocking extends AbstractCloseable {

  private final RandomAccessFile fRandomAccessFile;
  private final FileLock         fLock;

  public FileLocking(File file) throws IOException {
    fRandomAccessFile = new RandomAccessFile(file, "rw");
    FileLock lock = fRandomAccessFile.getChannel().tryLock();
    if (lock == null) {
      throw new IOException("File is already locked: " + file.getAbsolutePath());
    }
    fLock = lock;
  }

  @Override
  protected void internalClose() throws IOException {
    super.internalClose();
    fLock.release();
    fRandomAccessFile.close();
  }

}
