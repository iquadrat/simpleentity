package com.simpleentity.util.time;

import java.util.Date;

public class TimeStamp implements Comparable<TimeStamp> {

	private final long millisSinceEpoch;

	public TimeStamp(long millisSinceEpoch) {
		this.millisSinceEpoch = millisSinceEpoch;
	}

	public Date asDate() {
		return new Date(millisSinceEpoch);
	}
	
	@Override
	public int compareTo(TimeStamp other) {
		return Long.compare(millisSinceEpoch, other.millisSinceEpoch);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (millisSinceEpoch ^ (millisSinceEpoch >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeStamp other = (TimeStamp) obj;
		if (millisSinceEpoch != other.millisSinceEpoch)
			return false;
		return true;
	}

}
