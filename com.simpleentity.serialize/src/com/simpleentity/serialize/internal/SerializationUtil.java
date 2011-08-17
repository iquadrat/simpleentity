/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal;

import java.io.*;
import java.lang.reflect.*;

import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * @see Serializable
 * @author micha
 */
public class SerializationUtil {

  private SerializationUtil() {
    // utility class
  }

  public static Method getReadMethod(@CheckForNull Class<?> clazz) {
    if (clazz == null) return null;
    try {
      Method method = clazz.getDeclaredMethod("readObject", ObjectInputStream.class);
      if (Modifier.isPrivate(method.getModifiers())) return method;
    } catch (NoSuchMethodException e) {
    }
    return getReadMethod(clazz.getSuperclass());
  }
  
  public static Method getWriteReplaceMethod(Class<?> clazz) {
    try {
      return clazz.getDeclaredMethod("writeReplace");
    } catch (NoSuchMethodException e) {
      return null;
    }
  }
  
  public static Method getReadResolveMethod(Class<?> clazz) {
    try {
      return clazz.getDeclaredMethod("readResolve");
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  public static Method getWriteMethod(@CheckForNull Class<?> clazz) {
    if (clazz == null) return null;
    try {
      Method method = clazz.getDeclaredMethod("writeObject", ObjectOutputStream.class);
      if (Modifier.isPrivate(method.getModifiers())) return method;
    } catch (NoSuchMethodException e) {
    }
    return getWriteMethod(clazz.getSuperclass());
  }

}
