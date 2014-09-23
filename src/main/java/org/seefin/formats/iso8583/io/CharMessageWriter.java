package org.seefin.formats.iso8583.io;

import org.seefin.formats.iso8583.FieldTemplate;
import org.seefin.formats.iso8583.types.CharEncoder;
import org.seefin.formats.iso8583.types.Dimension;
import org.seefin.formats.iso8583.types.MTI;

import java.io.DataOutputStream;
import java.io.IOException;


/**
 * MessageWriter that encodes numeric fields as text (in defined Charset)
 * @author phillipsr
 */
public class CharMessageWriter
    extends MessageWriter {
  /**
   * Instantiate a character message writer that encodes the character
   * data in the specified character set
   * @param codec to be used when writing character data
   */
  public CharMessageWriter(CharEncoder codec) {
    super.charCodec = codec;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void appendMTI(MTI type, DataOutputStream output)
      throws IOException {
    write(type.toString(), output);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void appendField(FieldTemplate field, Object data, DataOutputStream output)
      throws IOException {
    byte[] fieldValue = charCodec.getBytes(field.format(data));
    Dimension dim = field.getDimension();
    if (dim.getType() == Dimension.Type.VARIABLE) {
      String vsize = String.format("%0" + dim.getVSize() + "d", fieldValue.length);
      output.write(charCodec.getBytes(vsize));
    }
    write(fieldValue, output);
  }

}
