package org.nulleins.formats.iso8583;

import junit.framework.Assert;
import org.junit.Test;
import org.nulleins.formats.iso8583.FieldTemplate;
import org.nulleins.formats.iso8583.MessageTemplate;
import org.nulleins.formats.iso8583.types.Bitmap;
import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.Dimension;
import org.nulleins.formats.iso8583.types.FieldType;
import org.nulleins.formats.iso8583.types.MTI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * basic test of programmatic API, creating a message template, adding fields and asserting
 * correct values
 * @author phillipsr
 */
public class TestMessageTemplate {
  private static final byte[] BINARY_BITMAP1 = new byte[]{(byte) 0xf2, 0x20, 0, 0, 0, 0, 0, 1};
  private static final byte[] BINARY_BITMAP2 = new byte[]{(byte) 0xc0, 0, 0, 0, 0, 0, 0, 1};
  private static final byte[] BINARY_BITMAP3 = new byte[]{0x40, 0, 0, 0, 0, 0, 0, 1};

  private static final MTI PaymentRequest = MTI.create("0200");
  private static final Dimension FIXED6 = Dimension.parse("fixed(6)");
  private static final Dimension FIXED10 = Dimension.parse("fixed(10)");

  @Test
  public void testCreateMessageTemplate() {
    MessageTemplate template = MessageTemplate.create("ISO015000077", PaymentRequest, BitmapType.BINARY);
    Assert.assertEquals(PaymentRequest, template.getMessageTypeIndicator());
    Assert.assertEquals("ISO015000077", template.getHeader());

    Map<Integer, FieldTemplate> field = new HashMap<Integer, FieldTemplate>() {{
      // int number, FieldType type, String dimension, String name, String description)
      put(2, new FieldTemplate(2, FieldType.ALPHA, Dimension.parse("llvar(10)"), "TestField", "Just a Test"));
      put(3, new FieldTemplate(3, FieldType.NUMERIC, FIXED6, "TestField", "Processing Code"));
      put(4, new FieldTemplate(4, FieldType.NUMERIC, FIXED6, "TestField", "Amount, transaction (PT - cents)"));
      put(7, new FieldTemplate(7, FieldType.DATE, FIXED10, "TestField", "Transmission Date and Time"));
      put(11, new FieldTemplate(11, FieldType.NUMERIC, FIXED6, "TestField", "System Trace Audit Number"));
    }};
    template.setFields(field);

    template.addField(new FieldTemplate(64, FieldType.NUMERIC, FIXED6, "TestField", "System Trace Audit Number"));
    template.addField(new FieldTemplate(66, FieldType.NUMERIC, FIXED6, "TestField", "System Trace Audit Number"));
    template.addField(new FieldTemplate(128, FieldType.NUMERIC, FIXED6, "TestField", "System Trace Audit Number"));
    template.addField(new FieldTemplate(130, FieldType.NUMERIC, FIXED6, "TestField", "System Trace Audit Number"));
    template.addField(new FieldTemplate(192, FieldType.NUMERIC, FIXED6, "TestField", "System Trace Audit Number"));

    // E220000000000001 becomes F220000000000001 as secondary bitmap now set
    Assert.assertEquals("F220000000000001", template.getBitmap().asHex(Bitmap.Id.PRIMARY));
    Assert.assertEquals("C000000000000001", template.getBitmap().asHex(Bitmap.Id.SECONDARY));
    Assert.assertEquals("4000000000000001", template.getBitmap().asHex(Bitmap.Id.TERTIARY));

    Assert.assertTrue(Arrays.equals(BINARY_BITMAP1, template.getBitmap().asBinary(Bitmap.Id.PRIMARY)));
    Assert.assertTrue(Arrays.equals(BINARY_BITMAP2, template.getBitmap().asBinary(Bitmap.Id.SECONDARY)));
    Assert.assertTrue(Arrays.equals(BINARY_BITMAP3, template.getBitmap().asBinary(Bitmap.Id.TERTIARY)));
  }

}
