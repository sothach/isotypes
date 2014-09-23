package org.seefin.formats.iso8583.types;

import junit.framework.Assert;
import org.junit.Test;


/**
 * @author phillipsr
 */
public class TestMTI {
  @Test
  public void testGoodMTI() {
    final MTI mti = MTI.create("0200");
    Assert.assertEquals("0200", mti.toString());
    Assert.assertEquals("ISO 8583-1:1987", mti.getVersion());
    Assert.assertEquals("Financial Message", mti.getMessageClass());
    Assert.assertEquals("Request", mti.getMessageFunction());
    Assert.assertEquals("Acquirer", mti.getMessageOrigin());
  }

  @Test
  public void testGoodMTIBinary() {
    final MTI mti = MTI.create(0x0200);
    Assert.assertEquals("0200", mti.toString());
    Assert.assertEquals("ISO 8583-1:1987", mti.getVersion());
    Assert.assertEquals("Financial Message", mti.getMessageClass());
    Assert.assertEquals("Request", mti.getMessageFunction());
    Assert.assertEquals("Acquirer", mti.getMessageOrigin());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMTINonNumeric() {
    MTI.create("A200");
  }

  @Test
  public void testAcquirerReversalAdviceRepeatMessage() {
    final MTI mti = MTI.create("0421");
    Assert.assertEquals("0421", mti.toString());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testMTIWrongFormat() {
    MTI.create("0206"); // '6' is not allowed in final position
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMTITooShort() {
    MTI.create("200");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMTITooLong() {
    MTI.create("02000");
  }

}
