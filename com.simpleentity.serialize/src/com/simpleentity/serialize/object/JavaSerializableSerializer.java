/*
 * Created on Dec 3, 2007
 *
 */
package com.simpleentity.serialize.object;

import java.lang.reflect.*;
import java.security.*;

import com.simpleentity.serialize.SerializationError;
import com.simpleentity.serialize.context.*;

import edu.umd.cs.findbugs.annotations.CheckForNull;

public class JavaSerializableSerializer<T> extends GenericObjectSerializer<T> {

  private final Method fReadResolveMethod;
  private final Method fWriteReplaceMethod;

  public JavaSerializableSerializer(Class<?> clazz, @CheckForNull Method readResolve, Method writeReplace) {
    super(clazz);
    fReadResolveMethod = readResolve;
    fWriteReplaceMethod = writeReplace;
    
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      public Void run() {
        if (fReadResolveMethod != null) fReadResolveMethod.setAccessible(true);
        if (fWriteReplaceMethod != null) fWriteReplaceMethod.setAccessible(true);
        return null;
      }
    });
    
  }

  @SuppressWarnings("unchecked")
  @Override
  public T deserialize(IObjectDeserializationContext context) {
    T object = super.deserialize(context);
    if (fReadResolveMethod == null) return object;
    
    try {
      return (T) fReadResolveMethod.invoke(object);
    } catch (IllegalArgumentException e) {
      throw new SerializationError(e);
    } catch (IllegalAccessException e) {
      throw new SerializationError(e);
    } catch (InvocationTargetException e) {
      throw new SerializationError(e);
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void serialize(IObjectSerializationContext context, T object) {
    T replacement = object;
    if (fWriteReplaceMethod != null) {
      try {
        replacement = (T) fWriteReplaceMethod.invoke(object);
      } catch (IllegalArgumentException e) {
        throw new SerializationError(e);
      } catch (IllegalAccessException e) {
        throw new SerializationError(e);
      } catch (InvocationTargetException e) {
        throw new SerializationError(e);
      }
    }
    super.serialize(context, replacement);
  }

}
