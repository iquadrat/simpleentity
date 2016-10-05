package com.simpleentity.serialize2.integration.testclasses;

import com.simpleentity.entity.value.ValueObject;


public class Key extends ValueObject implements Comparable<Key> {

  final int id;

  public Key(int id) {
    this.id = id;
  }

  @Override
  public int compareTo(Key o) {
    return Integer.compare(id, o.id);
  }

}