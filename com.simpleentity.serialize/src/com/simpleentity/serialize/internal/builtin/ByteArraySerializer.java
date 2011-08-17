/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;

import com.simpleentity.serialize.context.*;


public final class ByteArraySerializer extends AbstractArraySerializer {

  public ByteArraySerializer() {
    super(byte.class);
  }

  @Override
  protected void readElement(IObjectDeserializationContext context, Object array, int index) {
    Array.setByte(array, index, context.getReader().readByte());
  }
  
  @Override
  protected void writeElement(IObjectSerializationContext context, Object array, int i) {
    context.getWriter().putByte(Array.getByte(array, i));
  }

}
