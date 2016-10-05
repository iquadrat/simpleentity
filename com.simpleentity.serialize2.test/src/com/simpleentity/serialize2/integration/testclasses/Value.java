package com.simpleentity.serialize2.integration.testclasses;

import com.simpleentity.entity.value.ValueObject;


public class Value extends ValueObject implements Comparable<Value> {
  public final String name;
  public final double number;

  public Value(String name, double number) {
    this.name = name;
    this.number = number;
  }

  @Override
  public int compareTo(Value o) {
    return Double.compare(number, o.number);
  }
}