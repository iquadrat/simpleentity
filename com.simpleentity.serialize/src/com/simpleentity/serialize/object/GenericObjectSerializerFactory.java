/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.object;

import java.io.Serializable;
import java.lang.reflect.*;


import com.simpleentity.serialize.SerializationError;
import com.simpleentity.serialize.internal.SerializationUtil;
import com.simpleentity.serialize.internal.builtin.*;
import com.simpleentity.serialize.internal.entries.*;
import com.simpleentity.util.*;

public class GenericObjectSerializerFactory implements IObjectSerializerFactory {

  public static final IObjectSerializerFactory INSTANCE = new GenericObjectSerializerFactory();

  @Override
  public IObjectSerializer<?> getSerializer(Class<?> clazz) {

    if (clazz.isEnum()) {
      return new EnumSerializer(clazz);
    }
    if (clazz.isArray()) {
      return createArraySerializer(clazz);
    }

    if (Serializable.class.isAssignableFrom(clazz)) {
      return createJavaSerializableSerializer(clazz);
    }

    GenericObjectSerializer<?> serializer = new GenericObjectSerializer<Object>(clazz);
    createFieldSerializationEntries(clazz, serializer);
    return serializer;
  }

  private IObjectSerializer<?> createJavaSerializableSerializer(Class<?> clazz) {
    Method read = SerializationUtil.getReadMethod(clazz);
    Method write = SerializationUtil.getWriteMethod(clazz);
    Method readResolve = SerializationUtil.getReadResolveMethod(clazz);
    Method writeReplace = SerializationUtil.getWriteReplaceMethod(clazz);
    if (readResolve != null && read != null) {
      throw new SerializationError("Cannot handle classes which use readObject() and readResolve() at the same time!: " + clazz);
    }
    if (writeReplace != null && write != null) {
      throw new SerializationError("Cannot handle classes which use writeReplace() and write() at the same time!: " + clazz);
    }
    JavaSerializableSerializer<?> serializer = new JavaSerializableSerializer<Object>(clazz, readResolve, writeReplace);
    createFieldSerializationEntries(clazz, serializer);
    if (read != null || write != null) {
      serializer.add(new CustomReadWriteEntry(read, write));
    }
    return serializer;
  }

  public static void createFieldSerializationEntries(Class<?> clazz, GenericObjectSerializer<?> serializer) {
    for (Field field: ObjectUtil.getAllFields(clazz)) {
      // do not serialize transient fields
      if (Modifier.isTransient(field.getModifiers())) {
        continue;
      }
      ISerializationEntry fieldSerializer = getSerializer(field);
      serializer.add(fieldSerializer);
    }
  }

  private static <T> ISerializationEntry getSerializer(Field field) {
    Class<?> type = field.getType();
    if (type.isPrimitive()) {
      return getPrimitiveTypeSerializer(field, type);
    }
    if (type.isEnum()) {
      return new EnumSerializationEntry(field, type);
    }
    if (type.isAnnotation()) {
      throw Assert.fail("unexpected type: " + type);
    }
    return new ReferenceSerializationEntry(field);
  }

  private static ISerializationEntry getPrimitiveTypeSerializer(Field field, Class<?> type) {
    if (type.equals(boolean.class)) {
      return new BooleanSerializationEntry(field);
    }
    if (type.equals(byte.class)) {
      return new ByteSerializationEntry(field);
    }
    if (type.equals(char.class)) {
      return new CharSerializationEntry(field);
    }
    if (type.equals(short.class)) {
      return new ShortSerializationEntry(field);
    }
    if (type.equals(int.class)) {
      return new IntSerializationEntry(field);
    }
    if (type.equals(long.class)) {
      return new LongSerializationEntry(field);
    }
    if (type.equals(float.class)) {
      return new FloatSerializationEntry(field);
    }
    if (type.equals(double.class)) {
      return new DoubleSerializationEntry(field);
    }
    throw Assert.fail("unexpected primitve type: " + type);
  }

  private IObjectSerializer<?> createArraySerializer(Class<?> type) {
    Class<?> elementType = type.getComponentType();
    Assert.isNotNull(type);

    if (elementType.isPrimitive()) {
      return getPrimitiveArraySerializer(elementType);
    }
    if (elementType.isEnum()) {
      return new EnumArraySerializer(elementType);
    }
    if (elementType.isAnnotation()) {
      throw Assert.fail("unexpected type: " + type);
    }
    return new ReferenceArraySerializer(elementType);
  }

  private IObjectSerializer<?> getPrimitiveArraySerializer(Class<?> elementType) {

    if (elementType.equals(boolean.class)) {
      return new BooleanArraySerializer();
    }
    if (elementType.equals(byte.class)) {
      return new ByteArraySerializer();
    }
    if (elementType.equals(char.class)) {
      return new CharArraySerializer();
    }
    if (elementType.equals(short.class)) {
      return new ShortArraySerializer();
    }
    if (elementType.equals(int.class)) {
      return new IntArraySerializer();
    }
    if (elementType.equals(long.class)) {
      return new LongArraySerializer();
    }
    if (elementType.equals(float.class)) {
      return new FloatArraySerializer();
    }
    if (elementType.equals(double.class)) {
      return new DoubleArraySerializer();
    }
    throw Assert.fail("unexpected primitive type: " + elementType);
  }

  public IObjectSerializer<?> getSerializer(String className) {
    try {
      return getSerializer(Class.forName(className));
    } catch (ClassNotFoundException e) {
      throw Assert.fail(e);
    }
  }

  public IObjectSerializer<?> getSerializer(Object object) {
    return getSerializer(object.getClass());
  }

}
