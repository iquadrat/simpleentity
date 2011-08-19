/*
 * Created on Dec 3, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin.awt;

import java.awt.font.TextAttribute;
import java.lang.reflect.Field;
import java.security.*;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;


import com.simpleentity.serialize.context.*;
import com.simpleentity.serialize.object.IObjectSerializer;
import com.simpleentity.util.Assert;
import com.simpleentity.util.io.IOUtil;

public class TextAttributeSerializer implements IObjectSerializer<TextAttribute> {

  private final Field     fNameField;
  private final Map<?, ?> fInstanceMap;

  public TextAttributeSerializer() {
    try {

      fNameField = Attribute.class.getDeclaredField("name");
      final Field instanceMapField = TextAttribute.class.getDeclaredField("instanceMap");

      Assert.equals(fNameField.getType(), String.class);
      Assert.equals(instanceMapField.getType(), Map.class);

      AccessController.doPrivileged(new PrivilegedAction<Void>() {
        @Override
        public Void run() {
          fNameField.setAccessible(true);
          instanceMapField.setAccessible(true);
          return null;
        }
      });

      fInstanceMap = (Map<?, ?>) instanceMapField.get(null);

    } catch (SecurityException e) {
      throw Assert.fail(e);
    } catch (NoSuchFieldException e) {
      throw Assert.fail(e);
    } catch (IllegalArgumentException e) {
      throw Assert.fail(e);
    } catch (IllegalAccessException e) {
      throw Assert.fail(e);
    }
  }

  @Override
  public TextAttribute deserialize(IObjectDeserializationContext context) {
    String name = IOUtil.readStringUTF8(context.getReader());
    return (TextAttribute) fInstanceMap.get(name);
  }

  @Override
  public void serialize(IObjectSerializationContext context, TextAttribute object) {
    try {
      IOUtil.writeStringUTF8(context.getWriter(), (String) fNameField.get(object));
    } catch (IllegalArgumentException e) {
      Assert.fail(e);
    } catch (IllegalAccessException e) {
      Assert.fail(e);
    }
  }

}
