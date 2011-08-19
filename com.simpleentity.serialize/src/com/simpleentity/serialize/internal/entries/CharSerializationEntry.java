/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.lang.reflect.Field;

import com.simpleentity.serialize.context.*;

public class CharSerializationEntry extends AbstractFieldSerializationEntry {

  public CharSerializationEntry(Field field) {
    super(field);
  }

  @Override
  public void write(IObjectSerializationContext context, Object object) throws IllegalAccessException, IllegalArgumentException {
    context.getWriter().putChar(fField.getChar(object));
  }
  
  @Override
  public void read(IObjectDeserializationContext context, Object result) throws IllegalArgumentException, IllegalAccessException {
    fField.setChar(result, context.getReader().readChar());
  }

}
