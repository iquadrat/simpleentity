package com.simpleentity.serialize2.integration.testclasses;

import com.simpleentity.annotation.CheckForNull;
import com.simpleentity.annotation.ValueObject;

@ValueObject
public class ArrayRefs {
  public int[]  values;
  public int[]  values2;
  public char[] chars;
  @CheckForNull public Object ref;
}