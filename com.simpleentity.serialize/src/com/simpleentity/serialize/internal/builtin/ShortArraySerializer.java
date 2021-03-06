/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;

import com.simpleentity.serialize.context.*;

public final class ShortArraySerializer extends AbstractArraySerializer {

  public ShortArraySerializer() {
    super(short.class);
  }

  @Override
  protected void readElement(IObjectDeserializationContext context, Object array, int index) {
    Array.setShort(array, index, context.getReader().readShort());
  }

  @Override
  protected void writeElement(IObjectSerializationContext context, Object array, int i) {
    context.getWriter().putShort(Array.getShort(array, i));
  }

}
