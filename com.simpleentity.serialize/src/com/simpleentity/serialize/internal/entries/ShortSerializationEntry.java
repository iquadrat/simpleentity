/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.lang.reflect.Field;

import com.simpleentity.serialize.context.*;

public class ShortSerializationEntry extends AbstractFieldSerializationEntry {

  public ShortSerializationEntry(Field field) {
    super(field);
  }

  @Override
  public void write(IObjectSerializationContext context, Object object) throws IllegalAccessException, IllegalArgumentException {
    context.getWriter().putShort(fField.getShort(object));
  }
  
  @Override
  public void read(IObjectDeserializationContext context, Object result) throws IllegalArgumentException, IllegalAccessException {
    fField.setShort(result, context.getReader().readShort());
  }

}
