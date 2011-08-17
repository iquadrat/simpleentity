/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;

import com.simpleentity.serialize.context.*;


public final class CharArraySerializer extends AbstractArraySerializer {

  public CharArraySerializer() {
    super(char.class);
  }

  @Override
  protected void readElement(IObjectDeserializationContext context, Object array, int index) {
    Array.setChar(array, index, context.getReader().readChar());
  }
  
  @Override
  protected void writeElement(IObjectSerializationContext context, Object array, int i) {
    context.getWriter().putChar(Array.getChar(array, i));
  }

}
