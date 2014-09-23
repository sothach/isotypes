package org.seefin.formats.iso8583;

import org.junit.Before;
import org.junit.Test;
import org.seefin.formats.iso8583.types.BitmapType;
import org.seefin.formats.iso8583.types.Dimension;
import org.seefin.formats.iso8583.types.FieldType;
import org.seefin.formats.iso8583.types.MTI;

import java.math.BigInteger;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author phillipsr
 */
public class TestFields {
  private MessageTemplate messageTemplate;

  @Before
  public void setup() {
    messageTemplate = MessageTemplate.create("", MTI.create(0x0200), BitmapType.HEX);
    final MessageFactory schema = new MessageFactory();
    schema.initialize();
    messageTemplate.setSchema(schema);
  }

  @Test
  public void testNumeric()
      throws ParseException {
    final FieldTemplate target = new FieldTemplate(2, FieldType.NUMERIC, Dimension.parse("fixed(6)"), "test", "");
    target.setMessage(messageTemplate);
    final String intValue = new String(target.format(128));
    assertEquals("000128", intValue);

    final Object readBack = target.parse(intValue.getBytes());
    assertTrue(readBack instanceof BigInteger);
    assertEquals(BigInteger.valueOf(128), readBack);

    final String biValue = new String(target.format(BigInteger.TEN.multiply(BigInteger.TEN).add(BigInteger.TEN)));
    assertEquals("000110", biValue);

    final String strValue = new String(target.format(726161));
    assertEquals("726161", strValue);
  }

  @Test
  public void testLlvar() {
    final FieldTemplate target = new FieldTemplate(2, FieldType.ALPHANUM, Dimension.parse("llvar(3)"), "test", "");
    target.setMessage(messageTemplate);
    final String intValue = new String(target.format("128"));
    assertEquals("128", intValue);

  }

  @Test
  public void testLllvar() {
    final FieldTemplate target = new FieldTemplate(2, FieldType.ALPHASYMBOL, Dimension.parse("lllvar(11)"), "test", "");
    target.setMessage(messageTemplate);
    final String intValue = new String(target.format("Hello World"));
    assertEquals("Hello World", intValue);

  }

}
