package com.simpleentity.serialize2.reflect;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.simpleentity.serialize2.Instantiator;

public class ObjenesisInstantiator implements Instantiator {

	private final Objenesis objenesis = new ObjenesisStd(true);

	@Override
	public <T> T newInstance(Class<T> class_) {
		return class_.cast(objenesis.newInstance(class_));
	}

}
