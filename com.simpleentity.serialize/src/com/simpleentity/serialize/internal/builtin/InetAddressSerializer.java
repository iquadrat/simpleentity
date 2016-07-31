package com.simpleentity.serialize.internal.builtin;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.simpleentity.serialize.context.IObjectDeserializationContext;
import com.simpleentity.serialize.context.IObjectSerializationContext;
import com.simpleentity.serialize.object.IObjectSerializer;
import com.simpleentity.util.Assert;

public class InetAddressSerializer implements IObjectSerializer<InetAddress> {

	@Override
	public void serialize(IObjectSerializationContext context, InetAddress object) {
		byte[] bytes = object.getAddress();
		context.getWriter().putByteArray(bytes);
	}

	@Override
	public InetAddress deserialize(IObjectDeserializationContext context) {
		byte[] bytes = context.getReader().readByteArray();
		try {
			return InetAddress.getByAddress(bytes);
		} catch (UnknownHostException e) {
			throw Assert.fail(e);
		}
	}

}
