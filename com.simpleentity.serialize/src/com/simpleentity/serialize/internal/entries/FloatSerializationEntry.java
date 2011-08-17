/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.lang.reflect.Field;

import com.simpleentity.serialize.context.*;

public class FloatSerializationEntry extends AbstractFieldSerializationEntry {

  public FloatSerializationEntry(Field field) {
    super(field);
  }

  public void write(IObjectSerializationContext context, Object object) throws IllegalAccessException, IllegalArgumentException {
    context.getWriter().putFloat(fField.getFloat(object));
  }
  
  public void read(IObjectDeserializationContext context, Object result) throws IllegalArgumentException, IllegalAccessException {
    fField.setFloat(result, context.getReader().readFloat());
  }

}
