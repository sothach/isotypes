package org.nulleins.formats.iso8583.types;

import junit.framework.Assert;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.nulleins.formats.iso8583.types.BCD;

import java.math.BigInteger;


/**
 * @author phillipsr
 */
public class TestBCD {
  @Test
  public void testLength() {
    int length = 999;
    byte[] data = BCD.valueOf(length);
    Assert.assertTrue(ArrayUtils.isEquals(data, new byte[]{(byte) 0x09, (byte) 0x99}));

    length = 15;
    data = BCD.valueOf(length);
    Assert.assertTrue(ArrayUtils.isEquals(data, new byte[]{0x15}));
  }

  @Test
  public void testStringConvert() {
    String test = "1232199";
    byte[] data = BCD.valueOf(test);
    Assert.assertTrue(ArrayUtils.isEquals(data, new byte[]{0x01, 0x23, 0x21, (byte) 0x99}));
  }

  @Test
  public void testStringConvert2() {
    String test = "102";
    byte[] data = BCD.valueOf(test);
    Assert.assertTrue(ArrayUtils.isEquals(data, new byte[]{0x01, 0x02}));
  }

  @Test
  public void testStringConvertPadded() {
    String test = "001010";
    byte[] data = BCD.valueOf(test);
    Assert.assertTrue(ArrayUtils.isEquals(data, new byte[]{0x00, 0x10, 0x10}));
  }

  @Test
  public void testStringJustification() {
    byte[] data = new byte[]{0x08, 0x40};
    String result = BCD.toString(data);
    Assert.assertEquals("0840", result);
  }

  @Test
  public void testPaddingInteger() {
    int target = 123; // will need two bytes
    byte[] data = BCD.valueOf(target);
    Assert.assertTrue(data.length == 2);
    Assert.assertEquals(0x01, data[0]);
    Assert.assertEquals(0x23, data[1]);
  }

  @Test
  public void testPaddingLong() {
    long target = 5432818929192L; // 13 digits will need seven bytes
    byte[] data = BCD.valueOf(target);

    Assert.assertTrue(ArrayUtils.isEquals(data,
        new byte[]{(byte) 0x05, (byte) 0x43, (byte) 0x28,
            (byte) 0x18, (byte) 0x92, (byte) 0x91, (byte) 0x92}));
  }

  @Test
  public void testPaddingBigInteger() {
    BigInteger target = BigInteger.valueOf(5432818929192L); // 13 digits will need seven bytes
    byte[] data = BCD.valueOf(target);

    Assert.assertTrue(ArrayUtils.isEquals(data,
        new byte[]{(byte) 0x05, (byte) 0x43, (byte) 0x28,
            (byte) 0x18, (byte) 0x92, (byte) 0x91, (byte) 0x92}));
  }

  @Test
  public void testPaddingString() {
    String target = "123"; // will need two bytes
    byte[] data = BCD.valueOf(target);
    Assert.assertTrue(ArrayUtils.isEquals(data, new byte[]{(byte) 0x01, (byte) 0x23}));
  }

}
