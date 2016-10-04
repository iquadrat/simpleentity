package com.simpleentity.serialize2;

import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

public interface BinarySerializer<T> {

	public void serialize(T object, ByteWriter destination);

	public T deserialize(ByteReader source);

}
