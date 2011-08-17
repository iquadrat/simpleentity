/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;


import com.simpleentity.serialize.context.*;
import com.simpleentity.serialize.object.IObjectSerializer;
import com.simpleentity.util.io.IOUtil;

/**
 * Serializes constants of {@link Enum} by writing their ordinal.
 *  
 * @author micha
 */
public final class EnumSerializer implements IObjectSerializer<Enum<?>> {

  private final Enum<?>[] fEnumConstants;

  public EnumSerializer(Class<?> enumType) {
    fEnumConstants = (Enum<?>[]) enumType.getEnumConstants();
  }

  public void serialize(IObjectSerializationContext context, Enum<?> value) {
    IOUtil.writeIntCompact(context.getWriter(), value.ordinal());
  }

  public Enum<?> deserialize(IObjectDeserializationContext context) {
    int index = IOUtil.readIntCompact(context.getReader());
    return fEnumConstants[index];
  }

}
