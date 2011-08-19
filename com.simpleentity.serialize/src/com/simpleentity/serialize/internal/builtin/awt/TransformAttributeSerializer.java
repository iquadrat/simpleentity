/*
 * Created on Dec 3, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin.awt;

import java.awt.font.TransformAttribute;

import com.simpleentity.serialize.context.*;
import com.simpleentity.serialize.object.*;

public class TransformAttributeSerializer implements IObjectSerializer<TransformAttribute> {

  public final IObjectSerializer<TransformAttribute> fBase;
  
  public TransformAttributeSerializer() {
    GenericObjectSerializer<TransformAttribute> genericSerializer = new GenericObjectSerializer<TransformAttribute>(TransformAttribute.class);
    GenericObjectSerializerFactory.createFieldSerializationEntries(TransformAttribute.class, genericSerializer);
    fBase = genericSerializer;
  }
  
  @Override
  public void serialize(IObjectSerializationContext context, TransformAttribute object) {
    if (object.isIdentity()) {
      context.getWriter().putBoolean(true);
      return;
    }
    
    context.getWriter().putBoolean(false);
    fBase.serialize(context, object);
  }

  @Override
  public TransformAttribute deserialize(IObjectDeserializationContext context) {
    if (context.getReader().readBoolean()) {
      return TransformAttribute.IDENTITY;
    }
    return fBase.deserialize(context);
  }

}
