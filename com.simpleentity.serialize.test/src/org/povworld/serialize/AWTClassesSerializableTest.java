/*
 * Created on Dec 3, 2007
 *
 */
package org.povworld.serialize;

import java.awt.*;
import java.awt.color.*;
import java.awt.dnd.*;
import java.awt.event.InputEvent;
import java.awt.font.*;
import java.awt.geom.AffineTransform;

public class AWTClassesSerializableTest extends AbstractSerializationTest {

  public void test_serialize_ICC_Profile() {
    ICC_Profile profile = ICC_Profile.getInstance(ColorSpace.CS_GRAY);
    ICC_Profile actual = serializeAndDeserialize(profile);
    assertSame(profile, actual);
  }

  public void test_serialize_AWTKeyStroke() {
    AWTKeyStroke keyStroke = AWTKeyStroke.getAWTKeyStroke(83, InputEvent.ALT_DOWN_MASK);
    AWTKeyStroke actual = serializeAndDeserialize(keyStroke);
    assertEquals(keyStroke, actual);
  }

  public void test_serialize_TextAttribute() {
    assertSame(TextAttribute.FONT, serializeAndDeserialize(TextAttribute.FONT));
    assertSame(TextAttribute.JUSTIFICATION, serializeAndDeserialize(TextAttribute.JUSTIFICATION));
    assertSame(TextAttribute.SUPERSCRIPT, serializeAndDeserialize(TextAttribute.SUPERSCRIPT));
    assertSame(TextAttribute.RUN_DIRECTION, serializeAndDeserialize(TextAttribute.RUN_DIRECTION));
  }

  public void test_serialize_TransformAttribute() {
    TransformAttribute tattr = new TransformAttribute(new AffineTransform(1, 1, 1, 1, 1, 1));
    TransformAttribute actual = serializeAndDeserialize(tattr);
    assertEquals(tattr, actual);

    actual = serializeAndDeserialize(TransformAttribute.IDENTITY);
    assertSame(TransformAttribute.IDENTITY, actual);
  }

  public void test_serialize_drag_n_drop() {
    try {
      
      DragSource source = new DragSource();
      DragSource sactual = serializeAndDeserialize(source);
      assertEquals(source.getClass(), sactual.getClass());
      
//      DropTarget target = new DropTarget(); // FIXME
//      DropTarget tactual = serializeAndDeserialize(target);
//      assertEquals(target.getClass(), tactual.getClass());
      
    } catch (HeadlessException e) {
      // ignore test if headless
    }
  }

}
