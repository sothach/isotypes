package org.seefin.formats.iso8583.types;

import java.util.BitSet;

/**
 * helper methods to convert bitsets to- and from hex format, and to
 * convert to- and from appropriately ordered byte arrays, consistent
 * with the ISO8583 bitmap usage (i.e., big-endian)
 * @author phillipsr
 */
public class BitsetUtil {

  /**
   * Answer with a bitmap equivalent to the hexadecimal string supplied
   * @param hex string representation of an ISO8583 bitmap
   * @return a bitset initialized from the input hex string
   * @throws IllegalArgumentException if the hex string is null, empty or not even-sized
   */
  static BitSet
  hex2Bitset(String hex) {
    BitSet result = new BitSet();
    if (hex == null) {
      throw new IllegalArgumentException("Hex string must be non-null");
    }
    if (hex.length() == 0 || hex.length() % 2 != 0) {
      throw new IllegalArgumentException("Hex string must be even-sized (length=" + hex.length() + ")");
    }
    int length = hex.length();
    int bytenum = (length / 2) - 1;
    for (int index = length; index >= 2; index -= 2) {
      int bytevalue = Integer.valueOf(hex.substring(index - 2, index), 16);
      for (int bit = 0, mask = 0x80; mask >= 0x01; bit++, mask /= 2) {
        if ((mask & bytevalue) == mask) {
          result.set((bytenum * 8) + bit);
        }
      }
      bytenum--;
    }
    return result;
  }

  /**
   * Convert a bitset object to a hexadecimal string, as required by ISO8583 bitmap (hex)
   * format, of the specified length (right-passed with "00" if required)
   * @param bitset    bitmap to convert
   * @param minLength minimum required for the resulting hex string
   * @return hexadecimal string version of the <code>bitset</code>supplied
   */
  static String
  bitset2Hex(BitSet bitset, int minLength) {
    StringBuilder result = new StringBuilder();
    for (int bytenum = 0; bytenum < minLength / 2; bytenum++) {
      byte v = 0;
      for (int bit = 0, mask = 0x80; mask >= 0x01; bit++, mask /= 2) {
        if (bitset.get((bytenum * 8) + bit) == true) {
          v |= mask;
        }
      }
      result.append(String.format("%02X", v));
    }
    while (result.length() < minLength) {
      result.append("00");
    }
    return result.toString();
  }

  /**
   * Answer with a string representing the bitset provided, as a
   * sequence of zero and ones
   * @param target
   * @param length
   * @return
   * @throws IllegalArgumentException if the bitset is null
   */
  static String
  bitset2bitstring(BitSet target, int length) {
    if (target == null) {
      throw new IllegalArgumentException("Bitset must not be null");
    }
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < length; i++) {
      result.append(target.get(i) ? "1" : "0");
    }
    return result.toString();
  }

  /**
   * Answer with a BitSet initialized from a byte array representation of a bitmap
   * @param binBitmap
   * @return
   */
  static BitSet
  bin2Bitset(byte[] binBitmap) {
    BitSet result = new BitSet();
    for (int bytenum = 0; bytenum < binBitmap.length; bytenum++) {
      for (int bit = 0, mask = 0x80; mask >= 0x01; bit++, mask /= 2) {
        if ((mask & binBitmap[bytenum]) == mask) {
          result.set((bytenum * 8) + bit);
        }
      }
    }

    return result;
  }

  /**
   * Answer with a byte array representation of the supplied BitSet
   * @param bitSet
   * @param length
   * @return
   * @throws IllegalArgumentException if the bitset is null
   */
  static byte[]
  bitset2bin(BitSet bitSet, int length) {
    if (bitSet == null) {
      throw new IllegalArgumentException("bitSet must be non-null");
    }

    byte[] result = new byte[length];
    for (int bytenum = length - 1; bytenum >= 0; bytenum--) {
      result[bytenum] = 0;
      for (int bit = 0, mask = 0x80; mask >= 0x01; bit++, mask /= 2) {
        if (bitSet.get((bytenum * 8) + bit) == true) {
          result[bytenum] |= mask;
        }
      }
    }
    return result;
  }

}
