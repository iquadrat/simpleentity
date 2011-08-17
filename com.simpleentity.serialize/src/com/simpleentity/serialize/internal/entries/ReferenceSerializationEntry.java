/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.lang.reflect.Field;

import com.simpleentity.serialize.context.*;

public class ReferenceSerializationEntry extends AbstractFieldSerializationEntry {

  public ReferenceSerializationEntry(Field field) {
    super(field);
  }

  public void write(IObjectSerializationContext context, Object object) throws IllegalArgumentException, IllegalAccessException {
    context.writeObject(fField.get(object));
  }

  public void read(IObjectDeserializationContext context, final Object result) throws IllegalArgumentException, IllegalAccessException {
    context.readFieldReference(fField, result);
  }

}
