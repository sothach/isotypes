package org.seefin.formats.iso8583;

import junit.framework.Assert;
import org.apache.commons.collections.ListUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seefin.formats.iso8583.types.CardAcceptorLocation;
import org.seefin.formats.iso8583.types.MTI;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author phillipsr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestAutogen {
  @Resource
  private MessageFactory messages;

  private static final Matcher ExpectRequest = Pattern.compile(
      "0200F238801588E080100000000004000000195061189187162513461101010000002000000([0-9]{10})"
          + "([0-9]{6})10181812061206D00010000C00000000065890190611111100006702034117014641000000000000322"
          + "PH Rumukrushi          Porthar      PHNG5660162012201220122012100551031385").matcher("");

  /**
   * This test using a message schema that contains both default values (the processing code)
   * and autogen instructions, to populate the transmission time with the current date/time,
   * and the STAN field with a sequence number
   * @throws IOException
   */
  @Test
  public void messageGetsDefaultAndAutogenValue()
      throws IOException {
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("accountNumber", 5061189187162513461L);
      put("amount", 2000000);
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
    for (String line : message.describe()) {
      System.out.println(line);
    }
    String messageText = baos.toString();

    Assert.assertEquals(ListUtils.EMPTY_LIST, message.validate());
    Assert.assertTrue(ExpectRequest.reset(messageText).matches());

  }


}
