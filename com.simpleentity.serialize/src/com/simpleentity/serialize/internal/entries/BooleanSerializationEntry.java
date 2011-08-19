/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.lang.reflect.Field;

import com.simpleentity.serialize.context.*;

public class BooleanSerializationEntry extends AbstractFieldSerializationEntry {

  public BooleanSerializationEntry(Field field) {
    super(field);
  }

  @Override
  public void write(IObjectSerializationContext context, Object object) throws IllegalAccessException, IllegalArgumentException {
    context.getWriter().putBoolean(fField.getBoolean(object));
  }

  @Override
  public void read(IObjectDeserializationContext context, Object result) throws IllegalArgumentException, IllegalAccessException {
    fField.setBoolean(result, context.getReader().readBoolean());
  }

}
