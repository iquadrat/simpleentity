/*
 * Created on Dec 1, 2007
 *
 */
package com.simpleentity.serialize.object;

import java.util.*;

import com.simpleentity.serialize.IObjectSerializerRegistry;

/**
 * Cache which remembers and reuses object serializer created by a base serializer.
 *
 * @author micha
 *
 */
public class ObjectSerializerCache implements IObjectSerializerFactory, IObjectSerializerRegistry {

  private final Map<Class<?>, IObjectSerializer<?>> fObjectSerializerMap = new HashMap<Class<?>, IObjectSerializer<?>>();

  private final IObjectSerializerFactory            fFactory;

  public ObjectSerializerCache(IObjectSerializerFactory factory) {
    fFactory = factory;
  }

  /**
   * Registers a special serializer for the given class.
   */
  public <T> void registerSerializer(Class<T> clazz, IObjectSerializer<T> serializer) {
    fObjectSerializerMap.put(clazz, serializer);
  }

  public IObjectSerializer<?> getSerializer(Class<?> clazz) {
    IObjectSerializer<?> serializer = fObjectSerializerMap.get(clazz);
    if (serializer == null) {
      serializer = fFactory.getSerializer(clazz);
      fObjectSerializerMap.put(clazz, serializer);
    }
    return serializer;
  }

}
