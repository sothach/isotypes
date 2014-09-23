package org.seefin.formats.iso8583.io;

import org.seefin.formats.iso8583.FieldTemplate;
import org.seefin.formats.iso8583.types.CharEncoder;
import org.seefin.formats.iso8583.types.Dimension;
import org.seefin.formats.iso8583.types.MTI;

import java.io.DataInputStream;
import java.io.IOException;


/**
 * MessageReader that reads numeric fields as text (in the defined Charset)
 * @author phillipsr
 */
public class CharMessageReader
    extends MessageReader {
  /**
   * Instantiate a character message reader
   * @param input   stream to read the message from
   * @param charset character set that character data is expected to be encoded with
   */
  public CharMessageReader(CharEncoder charset) {
    super.charCodec = charset;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MTI readMTI(DataInputStream input)
      throws IOException {
    byte[] data = readBytes(4, input);
    return MTI.create(charCodec.getString(data));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] readField(FieldTemplate field, DataInputStream input)
      throws IOException {
    int length = field.getDimension().getLength();
    if (field.getDimension().getType() == Dimension.Type.VARIABLE) {
      byte[] data = readBytes(field.getDimension().getVSize(), input);
      length = Integer.parseInt(charCodec.getString(data));
    }
    byte[] result = null;
    try {
      result = readBytes(length, input);
    } catch (Exception e) {
      throw new IOException("Failed to field " + field + " from input stream", e);
    }
    return result;
  }


}
