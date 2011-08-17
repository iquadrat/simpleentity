/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;

import com.simpleentity.serialize.context.*;


public final class FloatArraySerializer extends AbstractArraySerializer {

  public FloatArraySerializer() {
    super(float.class);
  }

  @Override
  protected void readElement(IObjectDeserializationContext context, Object array, int index) {
    Array.setFloat(array, index, context.getReader().readFloat());
  }
  
  @Override
  protected void writeElement(IObjectSerializationContext context, Object array, int i) {
    context.getWriter().putFloat(Array.getFloat(array, i));
  }

}
