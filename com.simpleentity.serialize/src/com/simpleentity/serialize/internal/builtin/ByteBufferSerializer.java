package com.simpleentity.serialize.internal.builtin;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.InvalidMarkException;

import com.simpleentity.serialize.context.IObjectDeserializationContext;
import com.simpleentity.serialize.context.IObjectSerializationContext;
import com.simpleentity.serialize.object.IObjectSerializer;
import com.simpleentity.util.io.ByteBufferReader;
import com.simpleentity.util.io.ByteBufferWriter;

public class ByteBufferSerializer implements IObjectSerializer<ByteBuffer> {

	@Override
	public void serialize(IObjectSerializationContext context, ByteBuffer buffer) {
		ByteBufferWriter writer = context.getWriter();
		writer.putBoolean(buffer.isDirect());
		writer.putBoolean(buffer.order() == ByteOrder.BIG_ENDIAN);
		writer.putBoolean(buffer.isReadOnly());
		
		// Write bytes of the whole buffer.
		writer.putCompactIntUnsigned(buffer.capacity());
		ByteBuffer dup = buffer.duplicate();
		int mark = getMark(dup);
		dup.clear();
		writer.getByteBuffer().put(dup);

		writer.putCompactIntUnsigned(buffer.position());
		writer.putCompactIntUnsigned(buffer.limit());
		writer.putInt(mark);
	}

	private int getMark(ByteBuffer buffer) {
		try {
			return buffer.reset().position();
		} catch(InvalidMarkException e) {
			return -1;
		}
	}

	@Override
	public ByteBuffer deserialize(IObjectDeserializationContext context) {
		ByteBufferReader reader = context.getReader();
		boolean direct = reader.readBoolean();
		ByteOrder order = reader.readBoolean() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		boolean readOnly = reader.readBoolean();
		
		int capacity = reader.readCompactIntUnsigned();
		ByteBuffer result = direct ?
				ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
		result.order(order);
				
		// TODO optimize
		for(int i=0; i<capacity; ++i) {
			result.put(reader.getBuffer().get());
		}
		
		int position = reader.readCompactIntUnsigned();
		int limit = reader.readCompactIntUnsigned();
		int mark = reader.readInt();
		
		if (mark != -1) {
			result.position(mark);
			result.mark();
		}
		result.position(position);
		result.limit(limit);
				
		if (readOnly) {
			return result.asReadOnlyBuffer();
		}
		return result;
	}

}
