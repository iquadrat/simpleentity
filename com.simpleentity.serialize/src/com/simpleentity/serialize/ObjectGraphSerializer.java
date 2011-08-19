/*
 * Created on Nov 29, 2007
 *
 */
package com.simpleentity.serialize;

import java.awt.color.*;
import java.awt.font.*;
import java.nio.ByteBuffer;

import com.simpleentity.serialize.context.*;
import com.simpleentity.serialize.internal.builtin.*;
import com.simpleentity.serialize.internal.builtin.awt.*;
import com.simpleentity.serialize.object.*;

/**
 * Default implementation of {@link IObjectGraphSerializer}.
 * <p>
 * NOTE: This class is not thread-safe!
 * 
 * @author micha
 */
public class ObjectGraphSerializer implements IObjectGraphSerializer {

  private final ObjectSerializerCache fSerializerFactory;

  public ObjectGraphSerializer() {
    fSerializerFactory = new ObjectSerializerCache(GenericObjectSerializerFactory.INSTANCE);
    registerBuiltinObjectSerializers(this);
    registerBuiltinAWTSerializers(this);
  }
  
  @Override
  public <T> void registerSerializer(Class<T> clazz, IObjectSerializer<T> serializer) {
    fSerializerFactory.registerSerializer(clazz, serializer);
  }

  @SuppressWarnings("unchecked")
  public static void registerBuiltinObjectSerializers(IObjectSerializerRegistry serializer) {
    serializer.registerSerializer((Class<Class<?>>) Class.class.getClass(), new ClassSerializer());
    serializer.registerSerializer(String.class, new StringSerializer());
  }
  
  public static void registerBuiltinAWTSerializers(IObjectSerializerRegistry serializer) {
    serializer.registerSerializer(ICC_Profile.class, new ICCProfileSerializer<ICC_Profile>());
    serializer.registerSerializer(ICC_ProfileGray.class, new ICCProfileSerializer<ICC_ProfileGray>());
    serializer.registerSerializer(ICC_ProfileRGB.class, new ICCProfileSerializer<ICC_ProfileRGB>());
    serializer.registerSerializer(TextAttribute.class, new TextAttributeSerializer());
    serializer.registerSerializer(TransformAttribute.class, new TransformAttributeSerializer());
  }

  @Override
  public ByteBuffer[] serializeObjectGraph(Object rootObject) {
    SerializationContext context = new SerializationContext(fSerializerFactory, rootObject);
    return context.serializeObjectGraph();
  }

  @Override
  public Object deserializeObjectGraph(ByteBuffer bytes) {
    DeserializationContext context = new DeserializationContext(fSerializerFactory, bytes);
    return context.deserializeObjectGraph();
  }

}
