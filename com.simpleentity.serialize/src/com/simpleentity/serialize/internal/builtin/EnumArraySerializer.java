/*
 * Created on Dec 8, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;


import com.simpleentity.serialize.context.*;
import com.simpleentity.util.io.IOUtil;

public final class EnumArraySerializer extends AbstractArraySerializer {

  private final Enum<?>[] fEnumConstants;

  public EnumArraySerializer(Class<?> enumType) {
    super(enumType);
    fEnumConstants = (Enum<?>[]) enumType.getEnumConstants();
  }

  @Override
  protected void writeElement(IObjectSerializationContext context, Object array, int i) {
    Enum<?> value = (Enum<?>) Array.get(array, i);
    if (value == null) {
      IOUtil.writeIntCompact(context.getWriter(), 0);
    } else {
      IOUtil.writeIntCompact(context.getWriter(), value.ordinal() + 1);
    }
  }

  @Override
  protected void readElement(IObjectDeserializationContext context, Object result, int i) {
    int index = IOUtil.readIntCompact(context.getReader());
    Enum<?> value = (index == 0) ? null : fEnumConstants[index - 1];
    Array.set(result, i, value);
  }

}
