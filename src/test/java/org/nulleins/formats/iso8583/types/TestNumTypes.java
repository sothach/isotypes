package org.nulleins.formats.iso8583.types;

import junit.framework.Assert;
import org.junit.Test;
import org.nulleins.formats.iso8583.formatters.NumberFormatter;
import org.nulleins.formats.iso8583.formatters.TypeFormatter;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;


/**
 * @author phillipsr
 */
public class TestNumTypes {
  private TypeFormatter<BigInteger> formatter = new NumberFormatter(CharEncoder.ASCII);

  @Test
  public void testParseNumeric()
      throws ParseException {
    byte[] testData = "XXXX1234567ZXY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    byte[] data = FieldParser.getBytes(testData, pos, 5);
    BigInteger value = formatter.parse(FieldType.NUMERIC, Dimension.parse("FIXED(5)"), 5, data);

    Assert.assertEquals(BigInteger.valueOf(12345), value);
    Assert.assertEquals(9, pos.getIndex());
  }

  @Test(expected = ParseException.class)
  public void testParseNumericBad()
      throws ParseException {
    byte[] testData = "XXXX1234567ZXY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    try {
      byte[] data = FieldParser.getBytes(testData, pos, 8);
      formatter.parse(FieldType.NUMERIC, Dimension.parse("FIXED(8)"), 8, data);
    } catch (ParseException e) {
      Assert.assertEquals("Bad number format For input string: \"1234567Z\" for type=n [1234567Z]", e.getMessage());
      Assert.assertEquals(12, pos.getIndex());

      throw e;
    }
  }

  @Test
  public void testParseSignedNumeric()
      throws ParseException {
    byte[] testData = "XXXXD123456ZXY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    byte[] data = FieldParser.getBytes(testData, pos, 5);
    BigInteger value = formatter.parse(FieldType.NUMSIGNED, Dimension.parse("FIXED(5)"), 5, data);

    Assert.assertEquals(BigInteger.valueOf(-1234), value);
    Assert.assertEquals(9, pos.getIndex());
  }

  @Test
  public void testFormat() {
    byte[] data = formatter.format(FieldType.NUMERIC, 123, Dimension.parse("FIXED(6)"));

    Assert.assertTrue(data.length == 6);
    Assert.assertEquals("000123", new String(data));
  }

  @Test
  public void testFormatXNDebit() {
    byte[] data = formatter.format(FieldType.NUMSIGNED, -123, Dimension.parse("FIXED(5)"));

    Assert.assertTrue(data.length == 5);
    Assert.assertEquals("D0123", new String(data));
  }

  @Test
  public void testFormatXNDebitVar() {
    byte[] data = formatter.format(FieldType.NUMSIGNED, -123, Dimension.parse("LLVAR(5)"));

    Assert.assertTrue(data.length == 4);
    Assert.assertEquals("D123", new String(data));
  }

  @Test
  public void testFormatXNCredit() {
    byte[] data = formatter.format(FieldType.NUMSIGNED, 123, Dimension.parse("FIXED(5)"));

    Assert.assertTrue(data.length == 5);
    Assert.assertEquals("C0123", new String(data));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatXNTooLong() {
    try {
      formatter.format(FieldType.NUMSIGNED, -123456, Dimension.parse("FIXED(5)"));
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(e.getMessage().startsWith(
          "Field data length (7) exceeds field maximum (5)"));
      throw e;
    }
  }

  @Test
  public void testFormatExact() {
    byte[] data = formatter.format(FieldType.NUMERIC, 123456, Dimension.parse("FIXED(6)"));

    Assert.assertTrue(data.length == 6);
    Assert.assertEquals("123456", new String(data));
  }


  @Test
  public void testFormatNumVar() {
    byte[] testData = "123456".getBytes();
    Dimension dim = Dimension.parse("llvar(10)");

    byte[] result = formatter.format(FieldType.NUMERIC, testData, dim);
    Assert.assertTrue(Arrays.equals(testData, result));
  }

  @Test
  public void testFormatNumFix() {
    String testData = "123456";
    Dimension dim = Dimension.parse("fixed(10)");

    String result = new String(formatter.format(FieldType.NUMERIC, testData, dim));
    Assert.assertEquals("0000" + testData, result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNumTooLong() {
    byte[] testData = "1234567".getBytes();
    Dimension dim = Dimension.parse("llvar(2)");

    try {
      formatter.format(FieldType.NUMERIC, testData, dim);
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(e.getMessage().startsWith(
          "Field data length (7) exceeds field maximum (2)"));
      throw e;
    }
  }

}
