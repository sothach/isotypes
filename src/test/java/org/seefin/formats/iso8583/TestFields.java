package org.seefin.formats.iso8583;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.seefin.formats.iso8583.types.BitmapType;
import org.seefin.formats.iso8583.types.Dimension;
import org.seefin.formats.iso8583.types.FieldType;
import org.seefin.formats.iso8583.types.MTI;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.ParsePosition;


/**
 * @author phillipsr
 */
public class TestFields {
  private MessageTemplate messageTemplate;

  @Before
  public void setup() {
    messageTemplate = MessageTemplate.create("", MTI.create(0x0200), BitmapType.HEX);
    MessageFactory schema = new MessageFactory();
    schema.initialize();
    messageTemplate.setSchema(schema);
  }

  @Test
  public void testNumeric()
      throws ParseException {
    FieldTemplate target = new FieldTemplate(2, FieldType.NUMERIC, Dimension.parse("fixed(6)"), "test", "");
    target.setMessage(messageTemplate);
    String intValue = new String(target.format(128));
    Assert.assertEquals("000128", intValue);

    ParsePosition pos = new ParsePosition(0);
    Object readBack = target.parse(intValue.getBytes(), pos);
    Assert.assertTrue(readBack instanceof BigInteger);
    Assert.assertEquals(BigInteger.valueOf(128), readBack);

    String biValue = new String(target.format(BigInteger.TEN.multiply(BigInteger.TEN).add(BigInteger.TEN)));
    Assert.assertEquals("000110", biValue);

    String strValue = new String(target.format(726161));
    Assert.assertEquals("726161", strValue);
  }

  @Test
  public void testLlvar() {
    FieldTemplate target = new FieldTemplate(2, FieldType.ALPHANUM, Dimension.parse("llvar(3)"), "test", "");
    target.setMessage(messageTemplate);
    String intValue = new String(target.format("128"));
    Assert.assertEquals("128", intValue);

  }

  @Test
  public void testLllvar() {
    FieldTemplate target = new FieldTemplate(2, FieldType.ALPHASYMBOL, Dimension.parse("lllvar(11)"), "test", "");
    target.setMessage(messageTemplate);
    String intValue = new String(target.format("Hello World"));
    Assert.assertEquals("Hello World", intValue);

  }

}
