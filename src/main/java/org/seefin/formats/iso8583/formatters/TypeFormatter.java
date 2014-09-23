package org.seefin.formats.iso8583.formatters;

import org.seefin.formats.iso8583.MessageException;
import org.seefin.formats.iso8583.types.CharEncoder;
import org.seefin.formats.iso8583.types.Dimension;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Arrays;


/**
 * Generic definition of a type formatter, defining the format and parse
 * methods that concrete formatters need to support
 * @author phillipsr
 */
public abstract class TypeFormatter<T> {
  private CharEncoder charset;

  /**
   * Answer with a string representation of the data supplied, interpreted according to the
   * field type and dimension specification supplied
   * @param type      variant of the type specified for the field
   * @param dimension specifies if fixed or variable and the required size
   * @param position  of field value in the input
   * @param data      the bytes to be parsed
   * @throws IllegalArgumentException if the data is null or invalid the field type
   * @throws ParseException           if the data cannot be parsed to the specified type
   */
  public abstract T parse(String type, Dimension dimension, int position, byte[] data) throws ParseException;

  /**
   * Answer with a byte array representing the data supplied, formatted according to the
   * field type and dimension specified
   * @param type      variant of the alpha type specified for the field
   * @param data      the object to be formatted
   * @param dimension specifies if fixed or variable and the required size
   * @throws IllegalArgumentException if the data is null or invalid as an alpha string
   * @throws MessageException         if the data supplied results in the maximum field length being exceeded
   */
  public abstract byte[] format(String type, Object data, Dimension dimension);

  /**
   * Is the supplied value a valid instance of the type/dimension specified?
   * @param value     candidate value to store in field
   * @param type      (sub-type) of the field
   * @param dimension storage type & size information
   * @return
   */
  public abstract boolean isValid(Object value, String type, Dimension dimension);

  /**
   * Specify the charset to be used when reading or writing character data
   * @param charset to be used when formatting alpha-type field values
   *                (see {@link java.nio.charset.Charset})
   * @throws IllegalArgumentException if the charset is null, or not supported by the JVM
   */
  protected void setCharset(CharEncoder charset) {
    if (charset == null) {
      throw new IllegalArgumentException("charset cannot be null/empty");
    }
    this.charset = charset;
  }

  /**
   * Decode the supplied data using the configured charset
   * @param data
   * @return A string representation of the data supplied, in the charset specified
   * @throw RuntimeException if data cannot be translated to the appropriate charset
   */
  protected String decode(byte[] data) {
    assert charset != null;
    try {
      return charset.getString(data);
    } catch (UnsupportedEncodingException e) {
      RuntimeException rethrow = new IllegalArgumentException(
          "Decoding error for field data: " + Arrays.toString(data));
      rethrow.initCause(e);
      throw rethrow;
    }
  }

}
