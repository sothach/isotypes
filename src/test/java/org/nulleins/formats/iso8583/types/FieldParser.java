package org.nulleins.formats.iso8583.types;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;

/**
 * Parser to read byte data from ISO8583 field data item
 * @author phillipsr
 */
public class FieldParser {
  /**
   * @param data
   * @param pos
   * @param size
   * @param dimension
   * @return
   * @throws ParseException
   */
  public static byte[] getBytes(byte[] data, ParsePosition pos, int length)
      throws ParseException {
    int start = pos.getIndex();
    if (start + length > data.length) {
      pos.setErrorIndex(start);
      throw new ParseException("Data exhausted", start);
    }
    byte[] result = Arrays.copyOfRange(data, start, start + length);
    pos.setIndex(start + length);
    return result;
  }

  /**
   * Read the Hollerith field length value from
   * the data
   * @param data
   * @param pos
   * @param vSize of the hollerithian specifier
   * @return
   */
  public static int getLength(byte[] data, ParsePosition pos, int vSize) {
    int start = pos.getIndex();
    String result = new String(Arrays.copyOfRange(data, start, start + vSize));
    pos.setIndex(start + vSize);
    return Integer.parseInt(result);
  }

}
