package com.simpleentity.testmodel;

import com.simpleentity.entity.value.ValueObject;

public class Song extends ValueObject {
	private final String title;
	// TODO replace by Java 8 Duration.
	private final Duration duration;

	public String getTitle() {
		return title;
	}

	public Duration getDuration() {
		return duration;
	}

	public Song(String title, Duration duration) {
		this.title = title;
		this.duration = duration;
	}
}
