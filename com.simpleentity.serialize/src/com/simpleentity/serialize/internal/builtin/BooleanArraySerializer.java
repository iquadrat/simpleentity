/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;

import com.simpleentity.serialize.context.*;


public final class BooleanArraySerializer extends AbstractArraySerializer {

  public BooleanArraySerializer() {
    super(boolean.class);
  }

  @Override
  protected void readElement(IObjectDeserializationContext context, Object array, int index) {
    Array.setBoolean(array, index, context.getReader().readBoolean());
  }
  
  @Override
  protected void writeElement(IObjectSerializationContext context, Object array, int i) {
    context.getWriter().putBoolean(Array.getBoolean(array, i));
  }

}
