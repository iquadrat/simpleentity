/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;


import com.simpleentity.serialize.context.*;
import com.simpleentity.serialize.object.IObjectSerializer;
import com.simpleentity.util.io.IOUtil;

/**
 * Serializes class by writing just their full java name.
 *
 * @author micha
 */
public final class ClassSerializer implements IObjectSerializer<Class<?>> {

  public void serialize(IObjectSerializationContext context, Class<?> clazz) {
    int id = context.getIdForClass(clazz);
    IOUtil.writeIntCompact(context.getWriter(), id);
  }

  public Class<?> deserialize(IObjectDeserializationContext context) {
    int id = IOUtil.readIntCompact(context.getReader());
    return context.getClassForId(id);
  }

}
