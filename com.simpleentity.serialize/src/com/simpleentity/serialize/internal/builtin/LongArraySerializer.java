/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;

import com.simpleentity.serialize.context.*;


public final class LongArraySerializer extends AbstractArraySerializer {

  public LongArraySerializer() {
    super(long.class);
  }

  @Override
  protected void readElement(IObjectDeserializationContext context, Object array, int index) {
    Array.setLong(array, index, context.getReader().readLong());
  }
  
  @Override
  protected void writeElement(IObjectSerializationContext context, Object array, int i) {
    context.getWriter().putLong(Array.getLong(array, i));
  }

}
