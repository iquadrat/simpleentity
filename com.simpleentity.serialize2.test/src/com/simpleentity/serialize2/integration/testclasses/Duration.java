package com.simpleentity.serialize2.integration.testclasses;

import com.simpleentity.annotation.Positive;
import com.simpleentity.entity.value.ValueObject;

public class Duration extends ValueObject {
	private final @Positive long seconds;
	private final @Positive int nanos;

	public Duration(long seconds, int nanos) {
		this.seconds = seconds;
		this.nanos = nanos;
	}

	public long getSeconds() {
		return seconds;
	}

	public int getNanos() {
		return nanos;
	}

	public static Duration add(Duration d1, Duration d2) {
		long nanos = d1.nanos + d2.nanos;
		long overflow = 0;
		if (nanos > 1e9) {
			nanos %= 1000000000;
			overflow = nanos / 1000000000;
		}
		return new Duration(d1.seconds + d2.seconds + overflow, (int)nanos);
	}
}
