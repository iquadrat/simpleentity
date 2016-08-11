/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.object;

import java.util.ArrayList;
import java.util.List;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import com.simpleentity.serialize.context.IObjectDeserializationContext;
import com.simpleentity.serialize.context.IObjectSerializationContext;
import com.simpleentity.util.Assert;

public class GenericObjectSerializer<T> implements IObjectSerializer<T> {

	private static final Objenesis OBJENESIS = new ObjenesisStd();

	private final List<ISerializationEntry> fSerializationEntries = new ArrayList<ISerializationEntry>();
	private final ObjectInstantiator fInstantiator;

	public GenericObjectSerializer(Class<?> clazz) {
		fInstantiator = OBJENESIS.getInstantiatorOf(clazz);
	}

	public void add(ISerializationEntry entry) {
		fSerializationEntries.add(entry);
	}

	@Override
	public void serialize(IObjectSerializationContext context, T object) {
		try {
			doSerialize(context, object);
		} catch (IllegalArgumentException e) {
			Assert.fail(e);
		} catch (IllegalAccessException e) {
			Assert.fail(e);
		}
	}

	private void doSerialize(IObjectSerializationContext context, T object) throws IllegalAccessException {
		for (ISerializationEntry entry : fSerializationEntries) {
			entry.write(context, object);
		}
	}

	@Override
	public T deserialize(IObjectDeserializationContext context) {
		try {
			return doDeserialize(context);
		} catch (IllegalArgumentException e) {
			throw Assert.fail(e);
		} catch (IllegalAccessException e) {
			throw Assert.fail(e);
		}
	}

	@SuppressWarnings("unchecked")
	private T doDeserialize(IObjectDeserializationContext context) throws IllegalAccessException {
		T result = (T) fInstantiator.newInstance();
		for (ISerializationEntry entry : fSerializationEntries) {
			entry.read(context, result);
		}
		return result;
	}

}
