package org.seefin.formats.iso8583.io;

import org.seefin.formats.iso8583.FieldTemplate;
import org.seefin.formats.iso8583.types.Bitmap;
import org.seefin.formats.iso8583.types.BitmapType;
import org.seefin.formats.iso8583.types.CharEncoder;
import org.seefin.formats.iso8583.types.MTI;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * Generic message writer that defers the specific field output to
 * the appropriate subclass (e.g., character or BCD data)
 * @author phillipsr
 */
public abstract class MessageWriter {
  /** specifies the character encoding for text data (ASCII, EBCDIC) */
  protected CharEncoder charCodec;

  protected void write(String data, DataOutputStream output)
      throws UnsupportedEncodingException, IOException {
    write(charCodec.getBytes(data), output);
  }

  protected void write(byte[] data, DataOutputStream output)
      throws UnsupportedEncodingException, IOException {
    output.write(data);
  }

  protected void write(byte data, DataOutputStream output)
      throws UnsupportedEncodingException, IOException {
    output.write(data);
  }

  /**
   * Write the supplied header string to the output stream
   * @param header
   * @param output stream to append data to
   * @throws IOException if the data could not be written tot he output stream
   */
  public void appendHeader(String header, DataOutputStream output)
      throws IOException {
    output.write(charCodec.getBytes(header));
  }

  /**
   * Write the Message Type Indicator to the output stream
   * @param type   of message (MTI)
   * @param output stream to append data to
   * @throws IOException if the data could not be written tot he output stream
   */
  public abstract void appendMTI(MTI type, DataOutputStream output) throws IOException;

  /**
   * Write the supplied field to the output stream
   * @param field  template describing field to be written
   * @param data   value of the field to output
   * @param output stream to append data to
   * @throws IOException if the data could not be written tot he output stream
   */
  public abstract void appendField(FieldTemplate field, Object data, DataOutputStream output) throws IOException;

  /**
   * Write the supplied bitmap to the output stream
   * @param bitmap to be written to message stream
   * @param type   of bitmap, hex or binary
   * @param output stream to append data to
   * @throws IOException if the data could not be written tot he output stream
   */
  public void appendBitmap(Bitmap bitmap, BitmapType type, DataOutputStream output)
      throws IOException {
    if (type == BitmapType.BINARY) {
      appendBinaryBitmap(bitmap, output);
      return;
    }
    appendHexBitmap(bitmap, output);
  }

  /**
   * Append a binary bitmap to the output stream
   * @param bitmap to be appended
   * @param output stream to append data to
   * @throws IOException if the data could not be written tot he output stream
   */
  private void appendBinaryBitmap(Bitmap bitmap, DataOutputStream output)
      throws IOException {
    output.write(bitmap.asBinary(Bitmap.Id.PRIMARY));
    if (bitmap.isBitmapPresent(Bitmap.Id.SECONDARY)) {
      output.write(bitmap.asBinary(Bitmap.Id.SECONDARY));
      if (bitmap.isBitmapPresent(Bitmap.Id.TERTIARY)) {
        output.write(bitmap.asBinary(Bitmap.Id.TERTIARY));
      }
    }
    return;
  }

  /**
   * Append a hex (character encoded) bitmap to the output stream
   * @param bitmap to be appended
   * @param output stream to append data to
   * @throws IOException if the data could not be written tot he output stream
   */
  private void appendHexBitmap(Bitmap bitmap, DataOutputStream output)
      throws IOException {
    byte[] bitmap1 = charCodec.getBytes(bitmap.asHex(Bitmap.Id.PRIMARY));
    output.write(bitmap1);
    if (bitmap.isBitmapPresent(Bitmap.Id.SECONDARY)) {
      byte[] bitmap2 = charCodec.getBytes(bitmap.asHex(Bitmap.Id.SECONDARY));
      output.write(bitmap2);
      if (bitmap.isBitmapPresent(Bitmap.Id.TERTIARY)) {
        byte[] bitmap3 = charCodec.getBytes(bitmap.asHex(Bitmap.Id.TERTIARY));
        output.write(bitmap3);
      }
    }
  }

}
