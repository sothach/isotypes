package org.seefin.formats.iso8583.formatters;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.seefin.formats.iso8583.types.CharEncoder;
import org.seefin.formats.iso8583.types.Dimension;

import java.text.ParseException;


/**
 * Formatter that can format and parse ISO8583 time format field value
 * @author phillipsr
 */
public class TimeFormatter
    extends TypeFormatter<LocalTime> {
  private final static DateTimeFormatter Formatter = DateTimeFormat.forPattern("HHmmss");

  public TimeFormatter(CharEncoder charset) {
    setCharset(charset);
  }

  /**
   * {@inheritDoc}
   * @throws ParseException if the data cannot be parsed as a valid time value
   */
  @Override
  public LocalTime parse(String type, Dimension dimension, int length, byte[] data)
      throws ParseException {
    try {
      return Formatter.parseLocalTime(decode(data));
    } catch (Exception e) {
      throw new ParseException("Cannot parse time for dimension: '" + type + ":" + length + "'", length);
    }
  }

  /**
   * {@inheritDoc}
   * @throws IllegalArgumentException if the data is null
   */
  @Override
  public byte[] format(String type, Object data, Dimension dimension) {
    if (data == null) {
      throw new IllegalArgumentException("Time value cannot be null");
    }
    return Formatter.print(getTime(data)).getBytes();
  }

  /**
   * Answer with a time representation of the data object supplied
   * @param data to convert to a time
   * @return LocalTime set from data supplied
   * @throws IllegalArgumentException if the data is null or not a valid time value
   */
  private LocalTime getTime(Object data) {
    if (data instanceof LocalTime) {
      return (LocalTime) data;
    }
    if (data instanceof java.util.Date) {
      return new LocalTime(((java.util.Date) data).getTime());
    }
    if (data instanceof java.sql.Date) {
      return new LocalTime(((java.sql.Date) data).getTime());
    }
    String timeString = String.format("%6.6s", data.toString().trim()).replaceAll(" ", "0");
    if (timeString.length() != 6) {
      throw new IllegalArgumentException("Invalid data [" + data + "]: cannot convert to time (" + timeString + ")");
    }
    return Formatter.parseLocalTime(timeString);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid(Object value, String type, Dimension dimension) {
    if (value == null) {
      return false;
    }
    if (value instanceof java.util.Date || value instanceof LocalTime) {
      return true;
    }
    String timeValue = value.toString().trim();
    if (timeValue.length() != 6) {
      return false;
    }
    try {
      Formatter.parseLocalTime(timeValue);
    } catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }

}
