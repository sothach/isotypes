package org.nulleins.formats.iso8583.types;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Holds the character set that should be used for text encoding and decoding,
 * and provides the methods for encoding/decoding to/from byte[]/String
 * @author phillipsr
 */
public class CharEncoder {
  public static final CharEncoder ASCII = new CharEncoder("US-ASCII");
  private final Charset charset;

  /**
   * Set the charset that should be used for writing text field values
   * @param charsetName JVM name of charset (see {@link java.nio.charset.Charset})
   * @throws IllegalArgumentException if the charset is null, or not supported by the JVM
   */
  public CharEncoder(final String charsetName) {
    if (charsetName == null || charsetName.isEmpty() == true) {
      throw new IllegalArgumentException("charset cannot be null/empty");
    }
    if (!Charset.isSupported(charsetName)) {
      throw new IllegalArgumentException("charset [" + charsetName + "] not supported by JVM");
    }
    this.charset = Charset.forName(charsetName);
  }

  @Override
  public String
  toString() {
    return charset.name();
  }

  /**
   * Answer with a String in this character encoding, initialized from the byte data supplied
   * @param data bytes to be converted
   * @return a String using this character encoding
   * @throws UnsupportedEncodingException if <code>charset</code> is not supported by the JVM
   */
  public String
  getString(final byte[] data)
      throws UnsupportedEncodingException {
    return new String(data, charset.name());
  }

  /**
   * Answer with a byte array in this character encoding, initialized from the byte data supplied
   * @param data String to be converted
   * @return a byte character using this character encoding
   * @throws UnsupportedEncodingException if <code>charset</code> is not supported by the JVM
   */
  public byte[]
  getBytes(final String data)
      throws UnsupportedEncodingException {
    return data.getBytes(charset.name());
  }

  /**
   * Answer with a byte array in this character encoding, initialized from the byte data supplied
   * @param data bytes to be converted
   * @return a byte character using this character encoding
   * @throws UnsupportedEncodingException if <code>charset</code> is not supported by the JVM
   */
  public byte[]
  getBytes(final byte[] data)
      throws UnsupportedEncodingException {
    return getBytes(new String(data));
  }

}
