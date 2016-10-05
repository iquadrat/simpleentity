package com.simpleentity.serialize2.integration.testclasses;

import java.util.List;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.annotation.ValueObject;

@ValueObject
public class GenericReference<T> {
	public final @CheckForNull T ref;
	public final @CheckForNull List<T> list;

	public GenericReference(T ref, List<T> list) {
		this.ref = ref;
		this.list = list;
	}
}