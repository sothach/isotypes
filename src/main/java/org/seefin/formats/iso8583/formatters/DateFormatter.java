package org.seefin.formats.iso8583.formatters;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.seefin.formats.iso8583.types.CharEncoder;
import org.seefin.formats.iso8583.types.Dimension;
import org.seefin.formats.iso8583.types.FieldType;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


/**
 * Formatter that can format and parse ISO8583 date field formats
 * @author phillipsr
 */
public class DateFormatter
    extends TypeFormatter<DateTime> {
  private final static Map<String, DateTimeFormatter> Formatters
      = new HashMap<String, DateTimeFormatter>(3) {{
    put(FieldType.DATE + ":10", DateTimeFormat.forPattern("MMddHHmmss"));
    put(FieldType.DATE + ":4", DateTimeFormat.forPattern("MMdd"));
    put(FieldType.EXDATE + ":4", DateTimeFormat.forPattern("yyMM"));
  }};

  public DateFormatter(CharEncoder charset) {
    setCharset(charset);
  }

  /**
   * {@inheritDoc}
   * @throws ParseException if the supplied data cannot be parsed as a date value
   */
  @Override
  public DateTime parse(String type, Dimension dim, int length, byte[] data)
      throws ParseException {
    DateTimeFormatter formatter = Formatters.get(type + ":" + length);
    if (formatter == null) {
      throw new ParseException("Formatter not found for date field, type=("
          + type + ":" + length + ") data=" + HexDumper.getHexDump(data), length);
    }
    try {
      return formatter.parseDateTime(decode(data));
    } catch (Exception e) {
      ParseException rethrow = new ParseException("Cannot parse date field value, type=("
          + type + ":" + length + ") data=" + HexDumper.getHexDump(data)
          + " [decoded=" + decode(data) + "]", length);
      rethrow.initCause(e);
      throw rethrow;
    }
  }

  /**
   * {@inheritDoc}
   * @throws IllegalArgumentException if the data is null or not a valid date value
   */
  @Override
  public byte[] format(String type, Object data, Dimension dimension) {
    if (data == null) {
      throw new IllegalArgumentException("Date value cannot be null");
    }
    DateTime dateTime = getDateValue(data);
    if (dateTime == null) {
      throw new IllegalArgumentException("Invalid data [" + data
          + "] expected Date, got a " + data.getClass().getCanonicalName());
    }
    return Formatters.get(type + ":" + dimension.getLength()).print(dateTime).getBytes();
  }

  public static DateTime getDateValue(Object data) {
    if (data instanceof DateTime) {
      return (DateTime) data;
    }
    if (data instanceof java.util.Date) {
      return new DateTime(((java.util.Date) data).getTime());
    }
    if (data instanceof java.sql.Date) {
      return new DateTime(((java.sql.Date) data).getTime());
    }

    String dateString = data.toString().trim();
    String key = FieldType.DATE + ":" + dateString.length();
    DateTimeFormatter formatter = Formatters.get(key);
    if (formatter == null) {
      throw new IllegalArgumentException("Invalid data [" + data + "]: cannot convert to date");
    }
    return formatter.parseDateTime(dateString);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid(Object value, String type, Dimension dim) {
    return validate(value, type);
  }

  public static boolean validate(Object value, String type) {
    if (value == null) {
      return false;
    }
    if (value instanceof java.util.Date || value instanceof DateTime) {
      return true;
    }
    String dateValue = value.toString().trim();
    if (dateValue.length() != 4 && dateValue.length() != 10) {
      return false;
    }
    try {
      Formatters.get(type + ":" + dateValue.length()).parseDateTime(dateValue);
    } catch (IllegalArgumentException e) {
      return false;
    }

    return true;
  }

}