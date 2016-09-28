package com.simpleentity.util;

import net.jcip.annotations.NotThreadSafe;

import org.povworld.collection.common.PreConditions;

import edu.umd.cs.findbugs.annotations.CheckForNull;

// TODO check if this is indeed faster than an AtomicReference
@NotThreadSafe
public class Reference<T> {

	@CheckForNull
	private T value;

	public void set(T value) {
		PreConditions.paramNotNull(value);
		this.value = value;
	}

	public T get() {
		PreConditions.conditionCheck("Value not set!", value != null);
		return value;
	}

}
