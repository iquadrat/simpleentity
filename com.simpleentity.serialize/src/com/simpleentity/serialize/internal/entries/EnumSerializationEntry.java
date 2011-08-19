/*
 * Created on Nov 28, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.lang.reflect.Field;


import com.simpleentity.serialize.context.*;
import com.simpleentity.util.io.IOUtil;

public class EnumSerializationEntry extends AbstractFieldSerializationEntry {

  private final Object[] fEnumConstants;

  public EnumSerializationEntry(Field field, Class<?> enumType) {
    super(field);
    fEnumConstants = enumType.getEnumConstants();
  }

  @Override
  public void write(IObjectSerializationContext context, Object object) throws IllegalAccessException, IllegalArgumentException {
    Enum<?> enumValue = (Enum<?>) fField.get(object);

    if (enumValue == null) {
      write(context, 0);
    } else {
      write(context, enumValue.ordinal() + 1);
    }
  }

  private void write(IObjectSerializationContext context, int i) {
    IOUtil.writeIntCompact(context.getWriter(), i);
  }

  @Override
  public void read(IObjectDeserializationContext context, Object result) throws IllegalArgumentException, IllegalAccessException {
    fField.set(result, readEnum(context));
  }

  private Object readEnum(IObjectDeserializationContext context) {
    int index = IOUtil.readIntCompact(context.getReader());
    if (index == 0) return null;
    return fEnumConstants[index-1];
  }

}
