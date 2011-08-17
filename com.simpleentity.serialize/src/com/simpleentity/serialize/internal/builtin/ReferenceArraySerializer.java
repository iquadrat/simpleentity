/*
 * Created on Nov 30, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;

import java.lang.reflect.Array;


import com.simpleentity.serialize.context.*;
import com.simpleentity.util.Assert;

public final class ReferenceArraySerializer extends AbstractArraySerializer {

  public ReferenceArraySerializer(Class<?> elementType) {
    super(elementType);
    Assert.isFalse(elementType.isPrimitive());
  }
  
  @Override
  protected void writeElement(IObjectSerializationContext context, Object array, int i) {
    context.writeObject(Array.get(array, i));
  }

  @Override
  protected void readElement(IObjectDeserializationContext context, Object array, int i) {
    context.readArrayReference(array, i);
  }

}
