package org.nulleins.formats.iso8583;

import junit.framework.Assert;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nulleins.formats.iso8583.Message;
import org.nulleins.formats.iso8583.MessageFactory;
import org.nulleins.formats.iso8583.types.MTI;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Test message transformation using EBCDIC character set encoding for text fields
 * @author phillipsr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EncodingTest {
  private static final String EBCDIC_CHARSET = "IBM1047";
  private static final String ExpectMessage =
      "ISO0150000770200F238000108A180000000004000000000135432818929192"
          + "00101000000000120012121212006666661212001212041029937278626262"
          + "ATM-10101       DUB87                                   12353863579271840003C10";

  @Resource(name = "ebcdicTest")
  private MessageFactory factory;

  @Test
  public void codecTest()
      throws IOException {
    Assert.assertTrue(Charset.isSupported(EBCDIC_CHARSET));
    Charset ebcdicCodec = Charset.forName(EBCDIC_CHARSET);
    Assert.assertNotNull(ebcdicCodec);

    byte[] ebcdta = "Hello World".getBytes(EBCDIC_CHARSET);
    byte[] expect = new byte[]{(byte) 0xc8, (byte) 0x85, (byte) 0x93, (byte) 0x93, (byte) 0x96,
        0x40, (byte) 0xe6, (byte) 0x96, (byte) 0x99, (byte) 0x93, (byte) 0x84};

    Assert.assertTrue(ArrayUtils.isEquals(expect, ebcdta));
  }

  @Test
  public void testCreateMessageEbcdic()
      throws ParseException, IOException {
    Assert.assertEquals("IBM1047", factory.getCharset().toString());
    Message message = getTestMessage();

    Assert.assertEquals(ListUtils.EMPTY_LIST, message.validate());

    byte[] messageData = factory.getMessageData(message);
    Assert.assertEquals(ExpectMessage, new String(messageData, EBCDIC_CHARSET));

    Message readback = factory.parse(messageData);
    Assert.assertEquals(ExpectMessage, new String(factory.getMessageData(readback), EBCDIC_CHARSET));
  }

  private Message getTestMessage()
      throws ParseException {
    Date testDate = (new SimpleDateFormat("ddMMyyyy:HHmmss")).parse("12122012:121200");
    Message message = factory.create(MTI.create(0x0200));
    message.setFieldValue("cardNumber", 5432818929192L);
    message.setFieldValue("processingCode", 1010);
    message.setFieldValue("amount", new BigInteger("1200"));
    message.setFieldValue("transDateTime", testDate);
    message.setFieldValue("stan", 666666);
    message.setFieldValue("transTimeLocal", testDate);
    message.setFieldValue("transDateLocal", testDate);
    message.setFieldValue("acquierID", 1029);
    message.setFieldValue("extReference", 937278626262L);
    message.setFieldValue("cardTermId", "ATM-10101");
    message.setFieldValue("cardTermName", "DUB87");
    message.setFieldValue("msisdn", 353863579271L);
    message.setFieldValue("currencyCode", 840);
    message.setFieldValue("originalData", BigInteger.TEN);
    return message;
  }
}
