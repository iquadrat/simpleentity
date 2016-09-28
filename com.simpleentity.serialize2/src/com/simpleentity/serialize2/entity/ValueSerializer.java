package com.simpleentity.serialize2.entity;

import com.simpleentity.serialize2.generic.GenericValue;

// TODO remove?
interface ValueSerializer {

	public GenericValue serialize(Object value);

	public Object deserialize(GenericValue value);

}
