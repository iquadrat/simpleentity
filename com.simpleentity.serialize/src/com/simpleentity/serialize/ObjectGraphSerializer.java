/*
 * Created on Nov 29, 2007
 *
 */
package com.simpleentity.serialize;

import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ProfileGray;
import java.awt.color.ICC_ProfileRGB;
import java.awt.font.TextAttribute;
import java.awt.font.TransformAttribute;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.nio.ByteBuffer;

import com.simpleentity.serialize.context.DeserializationContext;
import com.simpleentity.serialize.context.SerializationContext;
import com.simpleentity.serialize.internal.builtin.ByteBufferSerializer;
import com.simpleentity.serialize.internal.builtin.ClassSerializer;
import com.simpleentity.serialize.internal.builtin.InetAddressSerializer;
import com.simpleentity.serialize.internal.builtin.StringSerializer;
import com.simpleentity.serialize.internal.builtin.awt.ICCProfileSerializer;
import com.simpleentity.serialize.internal.builtin.awt.TextAttributeSerializer;
import com.simpleentity.serialize.internal.builtin.awt.TransformAttributeSerializer;
import com.simpleentity.serialize.object.GenericObjectSerializerFactory;
import com.simpleentity.serialize.object.IObjectSerializer;
import com.simpleentity.serialize.object.ObjectSerializerCache;

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
	public <T> void registerSerializer(Class<T> clazz, IObjectSerializer<? super T> serializer) {
		fSerializerFactory.registerSerializer(clazz, serializer);
	}

	@Override
	public <T> void registerSerializer(String className, IObjectSerializer<? super T> serializer) {
		fSerializerFactory.registerSerializer(className, serializer);
	}

	@SuppressWarnings("unchecked")
	public static void registerBuiltinObjectSerializers(IObjectSerializerRegistry serializer) {
		serializer.registerSerializer((Class<Class<?>>) Class.class.getClass(), new ClassSerializer());
		serializer.registerSerializer(String.class, new StringSerializer());
		serializer.registerSerializer(Inet4Address.class, new InetAddressSerializer());
		serializer.registerSerializer(Inet6Address.class, new InetAddressSerializer());
		serializer.registerSerializer("java.nio.HeapByteBuffer", new ByteBufferSerializer());
		serializer.registerSerializer("java.nio.DirectByteBuffer", new ByteBufferSerializer());
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
