package org.seefin.formats.iso8583.types;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.seefin.formats.iso8583.formatters.DateFormatter;
import org.seefin.formats.iso8583.formatters.TimeFormatter;
import org.seefin.formats.iso8583.formatters.TypeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;


/**
 * @author phillipsr
 */
public class TestDateTypes {
  private static final TypeFormatter<DateTime> dateFormatter = new DateFormatter(CharEncoder.ASCII);
  private static final TypeFormatter<LocalTime> timeFormatter = new TimeFormatter(CharEncoder.ASCII);

  @Test
  public void testParseDate()
      throws ParseException {
    byte[] testData = "XXXX0304054133ZXY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    byte[] data = FieldParser.getBytes(testData, pos, 10);
    DateTime value = dateFormatter.parse(FieldType.DATE, Dimension.parse("FIXED(10)"), 10, data);

    Assert.assertEquals("2000-03-04T05:41:33.000Z", value.toString());
    Assert.assertEquals(14, pos.getIndex());
  }

  @Test(expected = ParseException.class)
  public void testParseDateBad()
      throws ParseException {
    byte[] testData = "XXXX1234567ZXY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    try {
      byte[] data = FieldParser.getBytes(testData, pos, 10);
      dateFormatter.parse(FieldType.DATE, Dimension.parse("FIXED(10)"), 10, data);
    } catch (ParseException e) {
      Assert.assertTrue(e.getMessage().startsWith("Cannot parse date field value"));
      Assert.assertEquals(14, pos.getIndex());

      throw e;
    }
  }

  @Test
  public void testParseTime()
      throws ParseException {
    byte[] testData = "XXXX0304054133ZXY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    byte[] data = FieldParser.getBytes(testData, pos, 6);
    LocalTime value = timeFormatter.parse(FieldType.TIME, Dimension.parse("FIXED(6)"), 6, data);

    Assert.assertEquals("03:04:05.000", value.toString());
    Assert.assertEquals(10, pos.getIndex());
  }

  @Test(expected = ParseException.class)
  public void testParseBadTime()
      throws ParseException {
    byte[] testData = "XXXXO3O4O54133ZXY".getBytes();
    ParsePosition pos = new ParsePosition(4);

    byte[] data = FieldParser.getBytes(testData, pos, 6);
    timeFormatter.parse(FieldType.TIME, Dimension.parse("FIXED(6)"), 6, data);
  }

  @Test
  public void testFormatDate4()
      throws ParseException {
    DateFormat df = new SimpleDateFormat("dd-MM-yy");
    byte[] data = dateFormatter.format(FieldType.DATE, df.parse("01-12-2001"), Dimension.parse("FIXED(4)"));

    Assert.assertTrue(data.length == 4);
    Assert.assertEquals("1201", new String(data));
  }

  @Test
  public void testFormatDate10()
      throws ParseException {
    DateFormat df = new SimpleDateFormat("dd-MM-yy/HH:mm:ss");
    byte[] data = dateFormatter.format(FieldType.DATE, df.parse("01-12-2001/09:45:33"), Dimension.parse("FIXED(10)"));
    Assert.assertTrue(data.length == 10);
    Assert.assertEquals("1201094533", new String(data));
  }

  @Test
  public void testFormatExpDate()
      throws ParseException {
    DateFormat df = new SimpleDateFormat("dd-MM-yy");
    byte[] data = dateFormatter.format(FieldType.EXDATE, df.parse("01-12-2010"), Dimension.parse("FIXED(4)"));

    Assert.assertTrue(data.length == 4);
    Assert.assertEquals("1012", new String(data));
  }

  @Test
  public void testFormatTime()
      throws ParseException {
    DateFormat df = new SimpleDateFormat("HH:mm:ss");
    byte[] data = timeFormatter.format(FieldType.TIME, df.parse("19:26:07"), Dimension.parse("FIXED(6)"));

    Assert.assertTrue(data.length == 6);
    Assert.assertEquals("192607", new String(data));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNullDate() {
    dateFormatter.format(FieldType.DATE, null, Dimension.parse("FIXED(4)"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatBadDate() {
    dateFormatter.format(FieldType.DATE, "12121212", Dimension.parse("FIXED(4)"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFormatNullTime() {
    timeFormatter.format(FieldType.TIME, null, Dimension.parse("FIXED(6)"));
  }

  @Test //(expected=IllegalArgumentException.class) - no longer fails as value constrained to dimension specified
  public void testFormatBadTime() {
    timeFormatter.format(FieldType.TIME, "12121212", Dimension.parse("FIXED(6)"));
  }

}
