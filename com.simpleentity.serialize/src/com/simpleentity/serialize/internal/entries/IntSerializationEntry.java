/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.lang.reflect.Field;

import com.simpleentity.serialize.context.*;

public class IntSerializationEntry extends AbstractFieldSerializationEntry {

  public IntSerializationEntry(Field field) {
    super(field);
  }

  public void write(IObjectSerializationContext context, Object object) throws IllegalAccessException, IllegalArgumentException {
    context.getWriter().putInt(fField.getInt(object));
  }
  
  public void read(IObjectDeserializationContext context, Object result) throws IllegalArgumentException, IllegalAccessException {
    fField.setInt(result, context.getReader().readInt());
  }

}
