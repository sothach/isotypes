package org.seefin.formats.iso8583.types;

import junit.framework.Assert;
import org.junit.Test;
import org.seefin.formats.iso8583.formatters.AlphaFormatter;
import org.seefin.formats.iso8583.formatters.NumberFormatter;
import org.seefin.formats.iso8583.formatters.TypeFormatter;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.ParsePosition;


/**
 * @author phillipsr
 */
public class TestVarTypes {
  private TypeFormatter<String> alphaFormatter = new AlphaFormatter(CharEncoder.ASCII);
  private TypeFormatter<BigInteger> numberFormatter = new NumberFormatter(CharEncoder.ASCII);

  @Test
  public void testParseLLVAR()
      throws ParseException {
    byte[] testData = "XXXX03128YYY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    int length = FieldParser.getLength(testData, pos, 2);
    byte[] data = FieldParser.getBytes(testData, pos, length);
    Object value = alphaFormatter.parse(FieldType.ALPHANUMSYMBOL, Dimension.parse("LLVAR(10)"), data.length, data);

    Assert.assertEquals("128", value);
    Assert.assertEquals(9, pos.getIndex());
  }

  @Test(expected = ParseException.class)
  public void testParseLLVARBad()
      throws ParseException {
    byte[] testData = "XXXX0312".getBytes();
    ParsePosition pos = new ParsePosition(4);

    try {
      int length = FieldParser.getLength(testData, pos, 2);
      byte[] data = FieldParser.getBytes(testData, pos, length);
      numberFormatter.parse(FieldType.NUMERIC, Dimension.parse("LLVAR(2)"), data.length, data);
    } catch (ParseException e) {
      Assert.assertEquals("Data exhausted", e.getMessage());
      Assert.assertEquals(6, pos.getErrorIndex());
      throw e;
    }
  }

  @Test
  public void testFormatLLVARNumeric() {
    Long value = 5432818929192L;

    byte[] data = numberFormatter.format(FieldType.NUMERIC, value, Dimension.parse("LLLVAR(13)"));

    Assert.assertEquals(value.toString(), new String(data));
  }

  @Test
  public void testFormatLLLVAR() {
    String text = "Hello, Hollerithian World!";
    byte[] data = alphaFormatter.format(FieldType.ALPHASYMBOL, text, Dimension.parse("LLLVAR(26)"));

    Assert.assertEquals(text, new String(data));
  }

}
