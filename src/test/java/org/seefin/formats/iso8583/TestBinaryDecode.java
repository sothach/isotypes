package org.seefin.formats.iso8583;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;


/**
 * @author phillipsr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestBinaryDecode {
  @Resource
  private MessageFactory factory;

  //@org.junit.Ignore
  @Test
  public void testFromBcdEncoding()
      throws IOException, ParseException {
    /*
		 * this test uses examples from a web site, to correlate the format created, see:
		 * http://www.automationz.co.nz/ISO8583Decode/ISO8583Decode.htm
		 */
    // 30 20 05 80 20 C0 00 04
    // 0011 0000 0010 0000 0000 0101 1000 0000 0010 0000 1100 0000 0000 0000 0000 0100
    // 34, 11, 22, 24, 25, 35, 41, 42, 62
    byte[] messageData = {
        0x02, 0x00, 0x30, 0x20, 0x05, (byte) 0x80, 0x20, (byte) 0xc0, 0x00, 0x04, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x38, 0x00, 0x00, 0x16, 0x38, 0x05, 0x23, 0x00, 0x03, 0x00, 0x37, 0x37, 0x12, 0x34, 0x56,
        0x78, (byte) 0x90, 0x00, 0x6d, 0x06, 0x12, 0x10, 0x10, 0x20, 0x20, 0x37, 0x61, 0x00, 0x00, 0x0f, 0x31,
        0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
        0x36, 0x20, 0x20, 0x20, 0x20, 0x20, 0x00, 0x06, 0x30, 0x30, 0x31, 0x36, 0x33, 0x38};

    Message message = factory.parse(messageData);

    System.out.println(message.describe().toString());

    Assert.assertEquals(BigInteger.valueOf(4000), message.getFieldValue(3));
    Assert.assertEquals(BigInteger.valueOf(3800), message.getFieldValue(4));
    Assert.assertEquals(BigInteger.valueOf(1638), message.getFieldValue(11));
    Assert.assertEquals(BigInteger.valueOf(523), message.getFieldValue(22));
    Assert.assertEquals(BigInteger.valueOf(3), message.getFieldValue(24));
    Assert.assertEquals(BigInteger.ZERO, message.getFieldValue(25));
    Object field35 = message.getFieldValue(35);
    Assert.assertTrue(field35 instanceof TrackData);
    TrackData track2data = (TrackData) field35;
    Assert.assertEquals(371234567890006L, track2data.getPrimaryAccountNumber());
    Assert.assertEquals(612, track2data.getExpirationDate());
    Assert.assertEquals(101, track2data.getServiceCode());
    Assert.assertEquals("12345678", message.getFieldValue(41));
    Assert.assertEquals("1234567896", message.getFieldValue(42));
    Assert.assertEquals("001638", message.getFieldValue(62));
  }


}
