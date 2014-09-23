package org.seefin.formats.iso8583;

import junit.framework.Assert;
import org.apache.commons.collections.ListUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seefin.formats.iso8583.types.CardAcceptorLocation;
import org.seefin.formats.iso8583.types.MTI;
import org.seefin.formats.iso8583.types.PostilionAddAmount;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author phillipsr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestBank {
  @Resource
  private MessageFactory messages;

  private static final String ExpectRequest =
      "0200F238801588E0801000000000040000001950611891871625134610110000000020000001206091818"
          + "00983110181812061206D00010000C00000000065890190611111100006702034117014641000000000000322"
          + "PH Rumukrushi          Porthar      PHNG5660162012201220122012100551031385";
  private static final String ExpectResponse =
      "0210F238801588E0841000000000040000001950611891871625134610110000000020000001206091818"
          + "00983110181812061206D00010000C00000000065890190611111100006702034117014641000000000000322"
          + "PH Rumukrushi          Porthar      PHNG5660601002566C0000024260261003566"
          + "C0000000000001001566C0000024260260162012201220122012100551031385";

  @Test
  public void testCreateMessage()
      throws ParseException, IOException {
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("accountNumber", 5061189187162513461L);
      put("processingCode", "011000");
      put("amount", 2000000);
      put("transDateTime", "1206091818");
      put("stan", "009831");
      put("transTimeLocal", "101818");
      put("transDateLocal", 1206);
      put("captureDate", "1206");
      put("transactionFee", -10000);
      put("processingFee", 0);
      put("acquierID", 589019);
      put("forwarderID", 111111);
      put("rrn", "000067020341");
      put("cardTermId", "17014641");
      put("cardAcceptorId", "000000000000322");
      put("cardAcceptorLoc", new CardAcceptorLocation("PH Rumukrushi", "Porthar", "PH", "NG"));
      put("currencyCode", 566);
      put("adviceCode", "2012201220122012");
      put("accountId1", "0551031385");
    }};

    Message message = messages.createByNames(MTI.create("0200"), params);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    messages.writeToStream(message, baos);
    String messageText = baos.toString();

    Assert.assertEquals(ListUtils.EMPTY_LIST, message.validate());
    Assert.assertEquals(ExpectRequest, messageText);

    // parse the message back as a map:
    Message readback = messages.parse(messageText.getBytes());
    Map<Integer, Object> outParams = readback.getFields();
    Assert.assertEquals(ListUtils.EMPTY_LIST, readback.validate());

    Assert.assertEquals(BigInteger.valueOf(5061189187162513461L), outParams.get(2));
    Assert.assertEquals(BigInteger.valueOf(11000), outParams.get(3));
    Assert.assertEquals(BigInteger.valueOf(2000000), outParams.get(4));
    Assert.assertEquals("2000-12-06T09:18:18.000Z", outParams.get(7).toString());
    Assert.assertEquals("9831", outParams.get(11).toString());
    Assert.assertEquals("10:18:18.000", outParams.get(12).toString());
    Assert.assertEquals("2000-12-06T00:00:00.000Z", outParams.get(13).toString());
    Assert.assertEquals("2000-12-06T00:00:00.000Z", outParams.get(17).toString());
    Assert.assertEquals(BigInteger.valueOf(10000).negate(), outParams.get(28));
    Assert.assertEquals(BigInteger.ZERO, outParams.get(30));
    Assert.assertEquals("589019", outParams.get(32).toString());
    Assert.assertEquals("111111", outParams.get(33).toString());
    Assert.assertEquals("000067020341", outParams.get(37).toString());
    Assert.assertEquals("17014641", outParams.get(41));
    Assert.assertEquals("000000000000322", outParams.get(42));
    Object f43 = outParams.get(43);
    Assert.assertTrue(f43 instanceof CardAcceptorLocation);
    CardAcceptorLocation cal = (CardAcceptorLocation) f43;
    Assert.assertEquals("PH Rumukrushi", cal.getLocation());
    Assert.assertEquals("Porthar", cal.getCity());
    Assert.assertEquals("PH", cal.getState());
    Assert.assertEquals("NG", cal.getCountry());
    Assert.assertEquals("566", outParams.get(49).toString());
    Assert.assertEquals("2012201220122012", outParams.get(60).toString());
    Assert.assertEquals("0551031385", outParams.get(102).toString());

    MTI responseType = MTI.create("0210");
    PostilionAddAmount[] addAmount = new PostilionAddAmount[3];
    addAmount[0] = new PostilionAddAmount(10, 2, 566, new BigInteger("2426026"));
    addAmount[1] = new PostilionAddAmount(10, 3, 566, BigInteger.ZERO);
    addAmount[2] = new PostilionAddAmount(10, 1, 566, new BigInteger("2426026"));
    //10 02 566 C 000002426026 10 03 566 C000000000000 10 01 566 C 000002426026

    outParams.put(54, addAmount);

    Message response = messages.createByNumbers(responseType, outParams);
    Assert.assertEquals(ListUtils.EMPTY_LIST, response.validate());
    baos = new ByteArrayOutputStream();
    messages.writeToStream(response, baos);
    String responseMessage = baos.toString();
    Assert.assertEquals(ExpectResponse, responseMessage);
  }


}
