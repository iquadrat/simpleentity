/*
 * Created on Dec 3, 2007
 *
 */
package com.simpleentity.serialize.internal.builtin.awt;

import java.awt.color.*;


import com.simpleentity.serialize.SerializationError;
import com.simpleentity.serialize.context.*;
import com.simpleentity.serialize.object.IObjectSerializer;
import com.simpleentity.util.io.IOUtil;

public class ICCProfileSerializer<T extends ICC_Profile> implements IObjectSerializer<T> {

  @Override
  @SuppressWarnings("unchecked")
  public T deserialize(IObjectDeserializationContext context) {

    // adapted from ICC_Profile#readObject()

    String csName = IOUtil.readStringUTF8(context.getReader());

    if (csName == null) {
      byte[] data = context.getReader().readByteArray();
      return (T) ICC_Profile.getInstance(data);
    }

    int cspace = 0; // ColorSpace.CS_* constant if known
    if (csName.equals("CS_sRGB")) {
      cspace = ColorSpace.CS_sRGB;
    } else if (csName.equals("CS_CIEXYZ")) {
      cspace = ColorSpace.CS_CIEXYZ;
    } else if (csName.equals("CS_PYCC")) {
      cspace = ColorSpace.CS_PYCC;
    } else if (csName.equals("CS_GRAY")) {
      cspace = ColorSpace.CS_GRAY;
    } else if (csName.equals("CS_LINEAR_RGB")) {
      cspace = ColorSpace.CS_LINEAR_RGB;
    } else {
      throw new SerializationError("Unknown profile: " + csName);
    }

    return (T) ICC_Profile.getInstance(cspace);

  }

  @Override
  public void serialize(IObjectSerializationContext context, T object) {

    // adapted from ICC_Profile#writeObject()

    String csName = null;
    if (object == ICC_Profile.getInstance(ColorSpace.CS_sRGB)) {
      csName = "CS_sRGB";
    } else if (object == ICC_Profile.getInstance(ColorSpace.CS_CIEXYZ)) {
      csName = "CS_CIEXYZ";
    } else if (object == ICC_Profile.getInstance(ColorSpace.CS_PYCC)) {
      csName = "CS_PYCC";
    } else if (object == ICC_Profile.getInstance(ColorSpace.CS_GRAY)) {
      csName = "CS_GRAY";
    } else if (object == ICC_Profile.getInstance(ColorSpace.CS_LINEAR_RGB)) {
      csName = "CS_LINEAR_RGB";
    }

    IOUtil.writeStringUTF8(context.getWriter(), csName);
    
    if (csName == null) {
      context.getWriter().putByteArray(object.getData());
    }
    
  }

}
