package com.simpleentity.serialize2.generic;


// TODO remove?
interface ValueSerializer {

	public GenericValue serialize(Object value);

	public Object deserialize(GenericValue value);

}
