package com.simpleentity.serialize2;

import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.povworld.collection.CollectionUtil;
import org.povworld.collection.common.ObjectUtil;

public class IterableMatcher<T> extends ArgumentMatcher<Iterable<T>> {

	private final Iterable<T> target;

	public IterableMatcher(Iterable<T> argument) {
		this.target = argument;
	}

	public static <T> Iterable<T> iteratesEq(Iterable<T> elements) {
		return Matchers.argThat(new IterableMatcher<T>(elements));
	}

	@Override
	public boolean matches(Object argument) {
		Iterable<?> iterable = ObjectUtil.castOrNull(argument, Iterable.class);
		if (iterable == null) {
			return false;
		}
		return CollectionUtil.iteratesEqualSequence(target, iterable);
	}

}