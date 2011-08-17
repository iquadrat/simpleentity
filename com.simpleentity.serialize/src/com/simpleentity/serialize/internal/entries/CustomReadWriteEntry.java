/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;


import com.simpleentity.serialize.SerializationError;
import com.simpleentity.serialize.context.*;
import com.simpleentity.serialize.object.ISerializationEntry;
import com.simpleentity.util.io.ByteBufferReader;
import com.simpleentity.util.io.ByteBufferWriter;
import com.simpleentity.util.io.IOUtil;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * Considers the special handling needed for classes which implement the
 * {@link Serializable} interface and make use of the <code>readObject</code>
 * or <code>writeObject</code> methods.
 * <p>
 * NOTE: Does not implement all aspects of Java's serialization protocol correctly.
 * 
 * @author micha
 */
public class CustomReadWriteEntry implements ISerializationEntry {

  private static final byte[] NO_DATA = new byte[0];
  
  private final Method fReadMethod;
  private final Method fWriteMethod;

  public CustomReadWriteEntry(@CheckForNull Method read, @CheckForNull Method write) {
    fReadMethod = read;
    fWriteMethod = write;
    
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      public Void run() {
        if (fReadMethod != null) fReadMethod.setAccessible(true);
        if (fWriteMethod != null) fWriteMethod.setAccessible(true);
        return null;
      }
    });
    
  }

  public void read(final IObjectDeserializationContext context, final Object result) {
    final byte[] data = readStoredData(context.getReader());
    if (fReadMethod == null) return;

    context.addPostDeserializationJob(new IPostDeserializationJob() {

      public void execute(final IObjectReader objectReader) {
        try {
          
          ByteBufferReader reader = new ByteBufferReader(ByteBuffer.wrap(data));
          fReadMethod.invoke(result, new ObjectIntputStreamAdapter(objectReader, reader));
          
        } catch (InvocationTargetException e) {
          fail(e);
        } catch (IOException e) {
          fail(e);
        } catch (IllegalArgumentException e) {
          fail(e);
        } catch (IllegalAccessException e) {
          fail(e);
        }
      }
      
    });
  }

  private byte[] readStoredData(ByteBufferReader reader) {
    if (fWriteMethod == null) {
      return NO_DATA;
    }
      
    int size = IOUtil.readIntCompact(reader);
    byte[] result = new byte[size];
    reader.read(result);
    return result;
  }

  public void write(final IObjectSerializationContext context, Object object) {
    if (fWriteMethod == null) return;
    
    ByteBuffer buffer = writeCustomSerialization(context, object);
    IOUtil.writeIntCompact(context.getWriter(), buffer.position());
    context.getWriter().write(buffer.array(), 0, buffer.position());
  }

  private ByteBuffer writeCustomSerialization(IObjectSerializationContext context, Object object) {
    try {
      
      ByteBufferWriter localStream = new ByteBufferWriter();
      ObjectOutputStreamAdapter ooStream = new ObjectOutputStreamAdapter(context, localStream);
      fWriteMethod.invoke(object, ooStream);
      return localStream.getByteBuffer();
      
    } catch (IOException e) {
      throw fail(e);
    } catch (IllegalArgumentException e) {
      throw fail(e);
    } catch (IllegalAccessException e) {
      throw fail(e);
    } catch (InvocationTargetException e) {
      throw fail(e);
    }
  }

  private Error fail(Throwable t) {
    throw new SerializationError(t);
  }

}
