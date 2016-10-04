package com.simpleentity.serialize2.binary;

import com.simpleentity.entity.id.EntityId;
import com.simpleentity.util.bytes.ByteReader;
import com.simpleentity.util.bytes.ByteWriter;

public class EntityIdSerializer implements BinarySerializer<EntityId> {

	@Override
	public void serialize(EntityId entityId, ByteWriter destination) {
		destination.putVarInt(entityId.getId());
	}

	@Override
	public EntityId deserialize(ByteReader source) {
		return new EntityId(source.getVarInt());
	}

}
