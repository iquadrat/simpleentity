package com.simpleentity.util.time;

public class SystemClock implements IClock {

	private static final IClock instance = new SystemClock();

	private SystemClock() {
	}

	@Override
	public TimeStamp now() {
		return new TimeStamp(System.currentTimeMillis());
	}

	public static IClock getInstance() {
		return instance;
	}

}
