/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;

import com.simpleentity.serialize.context.*;


public final class IntArraySerializer extends AbstractArraySerializer {

  public IntArraySerializer() {
    super(int.class);
  }

  @Override
  protected void readElement(IObjectDeserializationContext context, Object array, int index) {
    Array.setInt(array, index, context.getReader().readInt());
  }
  
  @Override
  protected void writeElement(IObjectSerializationContext context, Object array, int i) {
    context.getWriter().putInt(Array.getInt(array, i));
  }

}
