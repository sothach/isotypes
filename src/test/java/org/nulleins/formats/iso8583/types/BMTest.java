package org.nulleins.formats.iso8583.types;

import org.junit.Assert;
import org.junit.Test;
import org.nulleins.formats.iso8583.types.BitsetUtil;

import java.util.BitSet;


/**
 * @author phillipsr
 */
public class BMTest {
  @Test
  public void testBitmapHex() {
    BitSet target = BitsetUtil.hex2Bitset("4210001102C04804");

    String result = BitsetUtil.bitset2bitstring(target, 64);
    Assert.assertEquals("0100001000010000000000000001000100000010110000000100100000000100", result);

    result = BitsetUtil.bitset2Hex(target, 16);
    Assert.assertEquals("4210001102C04804", result);
  }

  @Test
  public void testBitmapBin() {
    BitSet target = BitsetUtil.bin2Bitset(
        new byte[]{(byte) 0x80, 0x04, 0x22, (byte) 0xf1, 0x01, 0x10, (byte) 0xc1, 0x01});

    // 10000000 00000100 00100010 11110001 00000001 00010000 11000001 00000001
    String result = BitsetUtil.bitset2Hex(target, 16);
    Assert.assertEquals("800422F10110C101", result);
  }

  @Test
  public void testBitmap2Bin() {
    BitSet target = BitsetUtil.hex2Bitset("4210001102C04804");

    byte[] result = BitsetUtil.bitset2bin(target, 8);
    BitSet bs = BitsetUtil.bin2Bitset(result);
    String string = BitsetUtil.bitset2Hex(bs, 16);
    Assert.assertEquals("4210001102C04804", string);
  }

  @Test
  public void testBitmapSetters() {
    BitSet target = new BitSet();
    target.set(0);
    target.set(63);

    String result = BitsetUtil.bitset2Hex(target, 16);
    Assert.assertEquals("8000000000000001", result);
  }

  @Test
  public void testBitmapGetters() {
    BitSet target = BitsetUtil.hex2Bitset("4210001102C04804");
    // 2, 7, 12, 28, 32, 39, 41, 42, 50, 53, 62
    Assert.assertFalse(target.get(0));
    Assert.assertTrue(target.get(1));
    Assert.assertFalse(target.get(2));
    Assert.assertFalse(target.get(3));
    Assert.assertFalse(target.get(4));
    Assert.assertFalse(target.get(5));
    Assert.assertTrue(target.get(6));
    Assert.assertFalse(target.get(7));
    Assert.assertFalse(target.get(8));
    Assert.assertFalse(target.get(9));
    Assert.assertFalse(target.get(10));
    Assert.assertTrue(target.get(11));
    Assert.assertTrue(target.get(27));
    Assert.assertTrue(target.get(31));
    Assert.assertTrue(target.get(38));
    Assert.assertTrue(target.get(40));
    Assert.assertTrue(target.get(41));
    Assert.assertTrue(target.get(49));
    Assert.assertTrue(target.get(52));
    Assert.assertTrue(target.get(61));
  }

}
