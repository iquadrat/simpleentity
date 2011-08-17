/*
 * Created on Dec 2, 2007
 *
 */
package com.simpleentity.serialize.internal.entries;

import java.lang.reflect.Field;
import java.security.*;

import com.simpleentity.serialize.object.ISerializationEntry;

/**
 * Abstract serializer for fields.
 * @author micha
 */
public abstract class AbstractFieldSerializationEntry implements ISerializationEntry {

  protected final Field fField;
  
  protected AbstractFieldSerializationEntry(Field field) {
    fField = field;
    enableFieldAccess();
  }

  private void enableFieldAccess() {
    if (fField.isAccessible()) return;
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      public Void run() {
        fField.setAccessible(true);
        return null;
      }
    });
  }

}
