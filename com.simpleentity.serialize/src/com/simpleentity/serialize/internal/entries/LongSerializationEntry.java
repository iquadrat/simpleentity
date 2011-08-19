/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.lang.reflect.Field;

import com.simpleentity.serialize.context.*;

public class LongSerializationEntry extends AbstractFieldSerializationEntry {

  public LongSerializationEntry(Field field) {
    super(field);
  }

  @Override
  public void write(IObjectSerializationContext context, Object object) throws IllegalAccessException, IllegalArgumentException {
    context.getWriter().putLong(fField.getLong(object));
  }
  
  @Override
  public void read(IObjectDeserializationContext context, Object result) throws IllegalArgumentException, IllegalAccessException {
    fField.setLong(result, context.getReader().readLong());
  }

}
