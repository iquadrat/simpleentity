/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;


import com.simpleentity.serialize.context.*;
import com.simpleentity.serialize.object.IObjectSerializer;
import com.simpleentity.util.io.IOUtil;

/**
 * Abstract serializer for all arrays.
 *  
 * @author micha
 */
public abstract class AbstractArraySerializer implements IObjectSerializer<Object> {

  protected final Class<?> fElementType;

  public AbstractArraySerializer(Class<?> elementType) {
    fElementType = elementType;
  }

  @Override
  public Object deserialize(IObjectDeserializationContext context) {
    int length = IOUtil.readIntCompact(context.getReader());
    Object result = Array.newInstance(fElementType, length);
    for (int i = 0; i < length; ++i) {
      readElement(context, result, i);
    }
    return result;
  }

  @Override
  public void serialize(IObjectSerializationContext context, Object array) {
    int length = Array.getLength(array);
    IOUtil.writeIntCompact(context.getWriter(), length);
    for (int i = 0; i < length; ++i) {
      writeElement(context, array, i);
    }
  }

  protected abstract void readElement(IObjectDeserializationContext context, Object result, int i);

  protected abstract void writeElement(IObjectSerializationContext context, Object array, int i);

}