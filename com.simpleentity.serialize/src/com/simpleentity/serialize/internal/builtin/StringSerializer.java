/*
 * Created on Dec 1, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin;


import com.simpleentity.serialize.context.*;
import com.simpleentity.serialize.object.IObjectSerializer;
import com.simpleentity.util.io.IOUtil;

/**
 * Serializes strings as their value in UTF-8.
 * 
 * @author micha
 */
public final class StringSerializer implements IObjectSerializer<String> {
  
  public void serialize(IObjectSerializationContext context, String string) {
    IOUtil.writeStringUTF8(context.getWriter(), string);
  }

  public String deserialize(IObjectDeserializationContext context) {
    return IOUtil.readStringUTF8(context.getReader());
  }

}
