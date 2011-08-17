/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;

import com.simpleentity.serialize.context.*;


public final class DoubleArraySerializer extends AbstractArraySerializer {

  public DoubleArraySerializer() {
    super(double.class);
  }

  @Override
  protected void readElement(IObjectDeserializationContext context, Object array, int index) {
    Array.setDouble(array, index, context.getReader().readDouble());
  }
  
  @Override
  protected void writeElement(IObjectSerializationContext context, Object array, int i) {
    context.getWriter().putDouble(Array.getDouble(array, i));
  }

}
