package org.seefin.formats.iso8583.types;

import junit.framework.Assert;
import org.junit.Test;


/**
 * @author phillipsr
 */
public class TestBitmap {
  @Test
  public void testBitmap() {
    Bitmap target = new Bitmap();

		/* 
		 * 4210001102C04804	Fields 2, 7, 12, 28, 32, 39, 41, 42, 50, 53, 62
		 * Explanation of Bitmap (8 BYTE Primary Bitmap = 64 Bit) field 4210001102C04804
		 * BYTE1 : 0100 0010 = 42x (fields 2 and 7 are present)
		 * BYTE2 : 0001 0000 = 10x (field 12 is present)
		 * BYTE3 : 0000 0000 = 00x (no fields present)
		 * BYTE4 : 0001 0001 = 11x (fields 28 and 32 are present)
		 * BYTE5 : 0000 0010 = 02x (field 39 is present)
		 * BYTE6 : 1100 0000 = C0x (fields 41 and 42 are present)
		 * BYTE7 : 0100 1000 = 48x (fields 50 and 53 are present)
		 * BYTE8 : 0000 0100 = 04x (field 62 is present)
		 * 
		 * Expect:
		 * 01000010 00010000 00000000 00010001 00000010 11000000 01001000 00000100
		 * 42       10       00       11       02       C0       48       04
		 * 
		 * 00100000 00010010 00000011 01000000 10001000 00000000 00001000 01000010
		 */

    target.setField(2);
    target.setField(7);
    target.setField(12);
    target.setField(28);
    target.setField(32);
    target.setField(39);
    target.setField(41);
    target.setField(42);
    target.setField(50);
    target.setField(53);
    target.setField(62);

    Assert.assertTrue(target.isFieldPresent(2));
    Assert.assertTrue(target.isFieldPresent(7));
    Assert.assertTrue(target.isFieldPresent(12));
    Assert.assertTrue(target.isFieldPresent(28));
    Assert.assertTrue(target.isFieldPresent(32));
    Assert.assertTrue(target.isFieldPresent(39));
    Assert.assertTrue(target.isFieldPresent(41));
    Assert.assertTrue(target.isFieldPresent(42));
    Assert.assertTrue(target.isFieldPresent(50));
    Assert.assertTrue(target.isFieldPresent(53));
    Assert.assertTrue(target.isFieldPresent(62));

    String hexBitmap = target.asHex(Bitmap.Id.PRIMARY);
    Assert.assertEquals("4210001102C04804", hexBitmap);

    byte[] binaryBitmap = target.asBinary(Bitmap.Id.PRIMARY);
    Assert.assertEquals((byte) 0x42, binaryBitmap[0]);
    Assert.assertEquals((byte) 0x10, binaryBitmap[1]);
    Assert.assertEquals((byte) 0x00, binaryBitmap[2]);
    Assert.assertEquals((byte) 0x11, binaryBitmap[3]);
    Assert.assertEquals((byte) 0x02, binaryBitmap[4]);
    Assert.assertEquals((byte) 0xc0, binaryBitmap[5]);
    Assert.assertEquals((byte) 0x48, binaryBitmap[6]);
    Assert.assertEquals((byte) 0x04, binaryBitmap[7]);
  }

  @Test
  public void testSecondaryBitmap() {
    Bitmap target = new Bitmap();

    target.setField(2);
    target.setField(66);

    Assert.assertTrue(target.isFieldPresent(2));
    Assert.assertTrue(target.isFieldPresent(66));
    Assert.assertTrue(target.isBitmapPresent(Bitmap.Id.PRIMARY));
    Assert.assertTrue(target.isBitmapPresent(Bitmap.Id.SECONDARY));
    Assert.assertFalse(target.isBitmapPresent(Bitmap.Id.TERTIARY));

    String hexBitmap1 = target.asHex(Bitmap.Id.PRIMARY);
    // should be "1100000..." as first bit specifies secondary bitmap present
    Assert.assertEquals("C000000000000000", hexBitmap1);

    byte[] binaryBitmap1 = target.asBinary(Bitmap.Id.PRIMARY);
    Assert.assertEquals((byte) 0xC0, binaryBitmap1[0]);

    String hexBitmap2 = target.asHex(Bitmap.Id.SECONDARY);
    Assert.assertEquals("4000000000000000", hexBitmap2);

    byte[] binaryBitmap2 = target.asBinary(Bitmap.Id.SECONDARY);
    Assert.assertEquals((byte) 0x40, binaryBitmap2[0]);
  }


  @Test
  public void testTertiaryBitmap() {
    Bitmap target = new Bitmap();

    target.setField(2);
    target.setField(140);

    Assert.assertTrue(target.isFieldPresent(2));
    Assert.assertTrue(target.isFieldPresent(140));
    Assert.assertTrue(target.isBitmapPresent(Bitmap.Id.PRIMARY));
    Assert.assertTrue(target.isBitmapPresent(Bitmap.Id.SECONDARY));
    Assert.assertTrue(target.isBitmapPresent(Bitmap.Id.TERTIARY));

    String hexBitmap1 = target.asHex(Bitmap.Id.PRIMARY);
    // should be "1100000..." as first bit specifies secondary bitmap present
    Assert.assertEquals("C000000000000000", hexBitmap1);

    byte[] binaryBitmap1 = target.asBinary(Bitmap.Id.PRIMARY);
    Assert.assertEquals((byte) 0xC0, binaryBitmap1[0]);

    String hexBitmap3 = target.asHex(Bitmap.Id.TERTIARY);
    Assert.assertEquals("0010000000000000", hexBitmap3);

    byte[] binaryBitmap3 = target.asBinary(Bitmap.Id.TERTIARY);
    Assert.assertEquals((byte) 0x10, binaryBitmap3[1]);
  }

  @Test
  public void testTerciaryBitmap() {
    Bitmap target = new Bitmap();

    target.setField(190);
    Assert.assertTrue(target.isBitmapPresent(Bitmap.Id.PRIMARY));
    Assert.assertTrue(target.isBitmapPresent(Bitmap.Id.SECONDARY));
    Assert.assertTrue(target.isBitmapPresent(Bitmap.Id.TERTIARY));

    String hexBitmap2 = target.asHex(Bitmap.Id.SECONDARY);
    // should be "1000000..." as first bit specifies secondary bitmap present
    Assert.assertEquals("8000000000000000", hexBitmap2);

    String hexBitmap3 = target.asHex(Bitmap.Id.TERTIARY);
    Assert.assertEquals("0000000000000004", hexBitmap3);

    byte[] binaryBitmap3 = target.asBinary(Bitmap.Id.TERTIARY);
    Assert.assertEquals((byte) 0x04, binaryBitmap3[7]);
  }

  private static final String HEX1 = "E440000000000008";
  private static final String HEX2 = "0000000000000040";

  @Test
  public void testBitmapParseHex1() {
    // 01100100 01000000 00000000 00000000 00000000 00000000 00000000 000010000
    Bitmap target = Bitmap.parse("6440000000000008");

    Assert.assertFalse(target.isFieldPresent(1));
    Assert.assertTrue(target.isFieldPresent(2));
    Assert.assertTrue(target.isFieldPresent(3));
    Assert.assertFalse(target.isFieldPresent(4));
    Assert.assertFalse(target.isFieldPresent(5));
    Assert.assertTrue(target.isFieldPresent(6));
    Assert.assertFalse(target.isFieldPresent(7));
    Assert.assertFalse(target.isFieldPresent(8));
    Assert.assertFalse(target.isFieldPresent(9));
    Assert.assertTrue(target.isFieldPresent(10));

    Assert.assertFalse(target.isBitmapPresent(Bitmap.Id.SECONDARY));

    String hexBitmap1 = target.asHex(Bitmap.Id.PRIMARY);
    Assert.assertEquals("6440000000000008", hexBitmap1);

    String hexBitmap2 = target.asHex(Bitmap.Id.SECONDARY);
    Assert.assertEquals("0000000000000000", hexBitmap2);

    String hexBitmap3 = target.asHex(Bitmap.Id.TERTIARY);
    Assert.assertEquals("0000000000000000", hexBitmap3);

  }

  @Test
  public void testBitmapParseHex2() {
    Bitmap target = Bitmap.parse(HEX1 + HEX2);
    Assert.assertTrue(target.isFieldPresent(1));
    Assert.assertTrue(target.isFieldPresent(2));
    Assert.assertTrue(target.isFieldPresent(3));
    Assert.assertTrue(target.isFieldPresent(6));

    Assert.assertTrue(target.isBitmapPresent(Bitmap.Id.SECONDARY));

    String hexBitmap1 = target.asHex(Bitmap.Id.PRIMARY);
    Assert.assertEquals("E440000000000008", hexBitmap1);

    String hexBitmap2 = target.asHex(Bitmap.Id.SECONDARY);
    Assert.assertEquals("0000000000000040", hexBitmap2);

    String hexBitmap3 = target.asHex(Bitmap.Id.TERTIARY);
    Assert.assertEquals("0000000000000000", hexBitmap3);

    byte[] binaryBitmap3 = target.asBinary(Bitmap.Id.TERTIARY);
    Assert.assertEquals((byte) 0x00, binaryBitmap3[7]);
  }

  private static final byte[] BIN
      = new byte[]{0x01, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08};

  @Test
  public void testBitmapParseBinary1() {
    Bitmap target = new Bitmap(BIN);
    byte[] binaryBitmap = target.asBinary(Bitmap.Id.PRIMARY);

    Assert.assertEquals((byte) 0x01, binaryBitmap[0]);
    Assert.assertEquals((byte) 0x40, binaryBitmap[1]);
    Assert.assertEquals((byte) 0x00, binaryBitmap[2]);
    Assert.assertEquals((byte) 0x00, binaryBitmap[3]);
    Assert.assertEquals((byte) 0x00, binaryBitmap[4]);
    Assert.assertEquals((byte) 0x00, binaryBitmap[5]);
    Assert.assertEquals((byte) 0x00, binaryBitmap[6]);
    Assert.assertEquals((byte) 0x08, binaryBitmap[7]);
  }

  // 10000010 01000000 00000000 00000000 00000000 00000000 00000000 000010000
  private static final byte[] BIN1
      = new byte[]{(byte) 0x82, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08};
  // 00001000 00000000 00000000 00000000 00000000 00000000 01000000 000000000
  private static final byte[] BIN2
      = new byte[]{0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x00};

  @Test
  public void testBitmapParseBinary2() {
    Bitmap target = new Bitmap(concatData(BIN1, BIN2));

    byte[] primaryBitmap = target.asBinary(Bitmap.Id.PRIMARY);
    byte[] secondaryBitmap = target.asBinary(Bitmap.Id.SECONDARY);

    Assert.assertEquals((byte) 0x82, primaryBitmap[0]);
    Assert.assertEquals((byte) 0x40, primaryBitmap[1]);
    Assert.assertEquals((byte) 0x00, primaryBitmap[2]);
    Assert.assertEquals((byte) 0x00, primaryBitmap[3]);
    Assert.assertEquals((byte) 0x00, primaryBitmap[4]);
    Assert.assertEquals((byte) 0x00, primaryBitmap[5]);
    Assert.assertEquals((byte) 0x00, primaryBitmap[6]);
    Assert.assertEquals((byte) 0x08, primaryBitmap[7]);

    Assert.assertEquals((byte) 0x08, secondaryBitmap[0]);
    Assert.assertEquals((byte) 0x00, secondaryBitmap[1]);
    Assert.assertEquals((byte) 0x00, secondaryBitmap[2]);
    Assert.assertEquals((byte) 0x00, secondaryBitmap[3]);
    Assert.assertEquals((byte) 0x00, secondaryBitmap[4]);
    Assert.assertEquals((byte) 0x00, secondaryBitmap[5]);
    Assert.assertEquals((byte) 0x40, secondaryBitmap[6]);
    Assert.assertEquals((byte) 0x00, secondaryBitmap[7]);

  }

  /**
   * @param bin12
   * @param bin22
   * @return
   */
  private byte[] concatData(byte[] data1, byte[] data2) {
    byte[] result = new byte[data1.length + data2.length];
    int index = 0;
    for (byte b : data1) {
      result[index++] = b;
    }
    for (byte b : data2) {
      result[index++] = b;
    }
    return result;
  }

  @Test(expected = IllegalArgumentException.class)
  public void
  testBitmapSetFieldMissing() {
    Bitmap target = Bitmap.parse("4210001102C04804");
    target.setField(1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void
  testBitmapHexNull() {
    Bitmap.parse(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void
  testBitmapHexBadLength() {
    Bitmap.parse("4210001102C0484");
  }

  @Test(expected = IllegalArgumentException.class)
  public void
  testBitmapNonHex() {
    Bitmap.parse("4210001102G04804");
  }

  @Test//(expected=IllegalArgumentException.class)
  public void
  testBitmapTertiary() {
    Bitmap.parse("4210001102C048044210001102C048044210001102C04804");
  }

}
