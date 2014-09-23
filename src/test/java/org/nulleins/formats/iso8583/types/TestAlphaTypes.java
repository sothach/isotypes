package org.nulleins.formats.iso8583.types;

import junit.framework.Assert;
import org.junit.Test;
import org.nulleins.formats.iso8583.MessageException;
import org.nulleins.formats.iso8583.formatters.AlphaFormatter;
import org.nulleins.formats.iso8583.formatters.TypeFormatter;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;


/**
 * @author phillipsr
 */
public class TestAlphaTypes {
  private static final TypeFormatter<String> formatter = new AlphaFormatter(CharEncoder.ASCII);

  @Test
  public void testParseAlpha()
      throws ParseException {
    byte[] testData = "123456Hello7ZXY".getBytes();
    ParsePosition pos = new ParsePosition(6);
    byte[] data = FieldParser.getBytes(testData, pos, 5);
    Object value = formatter.parse(FieldType.ALPHA, Dimension.parse("FIXED(5)"), 5, data);

    Assert.assertEquals("Hello", value);
    Assert.assertEquals(11, pos.getIndex());
  }


  @Test(expected = ParseException.class)
  public void testParseAlphaExhausted()
      throws ParseException {
    byte[] testData = "123456Hell".getBytes();
    ParsePosition pos = new ParsePosition(6);

    try {
      byte[] data = FieldParser.getBytes(testData, pos, 5);
      formatter.parse(FieldType.ALPHA, Dimension.parse("FIXED(5)"), 5, data);
    } catch (ParseException e) {
      Assert.assertEquals("Data exhausted", e.getMessage());
      Assert.assertEquals(6, pos.getErrorIndex());
      throw e;
    }
  }

  @Test(expected = ParseException.class)
  public void testParseAlphaInvalid()
      throws ParseException {
    byte[] testData = "123456H31109188".getBytes();
    ParsePosition pos = new ParsePosition(6);

    try {
      byte[] data = FieldParser.getBytes(testData, pos, 5);
      formatter.parse(FieldType.ALPHA, Dimension.parse("FIXED(5)"), 5, data);
    } catch (ParseException e) {
      Assert.assertTrue(e.getMessage().startsWith("Invalid data parsed"));
      throw e;
    }
  }

  @Test
  public void testFormatAlphaVar() {
    byte[] testData = "StringData".getBytes();
    Dimension dim = Dimension.parse("llvar(10)");

    byte[] result = formatter.format(FieldType.ALPHA, testData, dim);
    Assert.assertTrue(Arrays.equals(testData, result));
  }

  @Test
  public void testFormatAlphaFix() {
    String testData = "StringData";
    Dimension dim = Dimension.parse("fixed(12)");

    String result = new String(formatter.format(FieldType.ALPHA, testData, dim));
    Assert.assertEquals(testData + "  ", result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void
  testFormatAlphaInvalid() {
    formatter.format(FieldType.ALPHA, "1234", Dimension.parse("fixed(12)"));
  }

  @Test(expected = MessageException.class)
  public void
  testFormatAlphaFixTooLong() {
    formatter.format(FieldType.ALPHA, "StringData", Dimension.parse("fixed(2)"));
  }

  @Test(expected = MessageException.class)
  public void
  testFormatAlphaVarTooLong() {
    formatter.format(FieldType.ALPHA, "TooLong".getBytes(), Dimension.parse("llvar(2)"));
  }


}
