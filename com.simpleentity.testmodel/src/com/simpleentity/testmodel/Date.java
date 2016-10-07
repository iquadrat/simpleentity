package com.simpleentity.testmodel;

import org.povworld.collection.common.PreConditions;

import com.simpleentity.annotation.Positive;
import com.simpleentity.entity.value.ValueObject;

public class Date extends ValueObject {

	private final int year;
	private final @Positive byte month;
	private final @Positive byte day;

	public Date(int year, @Positive int month, @Positive int day) {
		this.year = year;
		this.day = PreConditions.paramPositive((byte)day);
		this.month = PreConditions.paramPositive((byte)month);
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	@Override
	public String toString() {
		return day + "." + month + "." + year;
	}
}
