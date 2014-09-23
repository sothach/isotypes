package org.nulleins.formats.iso8583.formatters;


/**
 * @author phillipsr
 */
public class HexDumper {
  public static String
  getHexDump(final byte[] data) {
    final StringBuilder result = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
      if (i > 0) {
        if (i % 16 == 0) {
          result.append("\n");
        } else {
          result.append(", ");
        }
      }
      result.append(String.format("0x%02x", data[i]));
    }
    return result.toString();
  }

}
