package org.nulleins.formats.iso8583;

import junit.framework.Assert;
import org.apache.commons.collections.ListUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nulleins.formats.iso8583.types.Bitmap;
import org.nulleins.formats.iso8583.types.MTI;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author phillipsr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestXsd {
  @Resource
  private MessageFactory messages;

  @Test
  public void
  testListXsdSchema() {
    Assert.assertNotNull(messages);

    MessageTemplate msg0200 = messages.getTemplate(MTI.create(0x0200));
    Assert.assertNotNull(msg0200);
    Assert.assertEquals(14, msg0200.getFields().size());

    MessageTemplate msg0400 = messages.getTemplate(MTI.create(0x0400));
    Assert.assertNotNull(msg0400);
    Assert.assertEquals(12, msg0400.getFields().size());
  }

  @Test
  public void testCreateMessage()
      throws ParseException, IOException {
    // create the test message and set field values:
    Message message = messages.create(MTI.create(0x0200));
    message.setFieldValue("cardNumber", new CardNumber(5432818929192L));
    message.setFieldValue("processingCode", 1010);
    message.setFieldValue("amount", new BigInteger("1200"));
    message.setFieldValue("transDateTime",
        (new SimpleDateFormat("MMddHHmmss")).parse("1212121212"));
    message.setFieldValue("stan", 666666);
    message.setFieldValue("transTimeLocal",
        (new SimpleDateFormat("HHmmss")).parse("121212"));
    message.setFieldValue("transDateLocal",
        (new SimpleDateFormat("MMdd")).parse("1212"));
    message.setFieldValue("acquierID", 1029);
    message.setFieldValue("extReference", 937278626262L);
    message.setFieldValue("cardTermId", "ATM-10101");
    message.setFieldValue("cardTermName", "DUB87");
    message.setFieldValue("msisdn", 353863579271L);
    message.setFieldValue("currencyCode", 840);
    message.setFieldValue("originalData", BigInteger.TEN);

    // check the message has been correctly created:
    Assert.assertEquals(ListUtils.EMPTY_LIST, message.validate());

    // convert the message into its wire format:
    byte[] messageData = messages.getMessageData(message);

    // parse the message back into a new message instance
    Message readback = messages.parse(messageData);
    // ensure the message context is the same as the original (allowing for type promotions):
    DateTime dateTime = DateTimeFormat.forPattern("MMddHHmmss").parseDateTime("1212121212");
    DateTime date = DateTimeFormat.forPattern("MMdd").parseDateTime("1212");
    LocalTime localTime = DateTimeFormat.forPattern("HHmmss").parseLocalTime("121212");
    Assert.assertEquals("5432*******92", readback.getFieldValue("cardNumber"));
    Assert.assertEquals(BigInteger.valueOf(1010), readback.getFieldValue("processingCode"));
    Assert.assertEquals(BigInteger.valueOf(1200), readback.getFieldValue("amount"));
    Assert.assertEquals(dateTime, readback.getFieldValue("transDateTime"));
    Assert.assertEquals(BigInteger.valueOf(666666), readback.getFieldValue("stan"));
    Assert.assertEquals(localTime, readback.getFieldValue("transTimeLocal"));
    Assert.assertEquals(date, readback.getFieldValue("transDateLocal"));
    Assert.assertEquals(BigInteger.valueOf(1029), readback.getFieldValue("acquierID"));
    Assert.assertEquals(BigInteger.valueOf(937278626262L), readback.getFieldValue("extReference"));
    Assert.assertEquals("ATM-10101", readback.getFieldValue("cardTermId"));
    Assert.assertEquals("DUB87", readback.getFieldValue("cardTermName"));
    Assert.assertEquals(BigInteger.valueOf(353863579271L), readback.getFieldValue("msisdn"));
    Assert.assertEquals(BigInteger.valueOf(840), readback.getFieldValue("currencyCode"));
    Assert.assertEquals(BigInteger.TEN, readback.getFieldValue("originalData"));

    // check the describer by comparing the description of the original message
    // with that of the read-back message; as the data-types can change in translation,
    // only the first 21 chars are compared (the field definitions)
    //
    Set<String> description = new HashSet<String>();
    for (String line : message.describe()) {
      description.add(line.substring(0, 21));
    }
    for (String line : readback.describe()) {
      Assert.assertTrue(description.contains(line.substring(0, 21)));
    }
  }

  @Test(expected = MessageException.class)
  public void testCreateBadMessage() {
    // create the test message and set field values:
    Message message = messages.create(MTI.create(0x0200));
    List<String> errors = message.validate();
    if (errors.isEmpty() == false) {
      throw new MessageException(errors);
    }
  }

  private static final String ExpectedBeanMessage =
      "ISO0150000770200F238000108A180000000004000000000" +
          "135432*******920010100000000000120923000000618172" +
          "1011121117041029937278626262ATM-10101       DUB87" +
          "                                   12353863579271840004C999";

  @Test
  public void testCreateMessageFromBean()
      throws ParseException {
    // this bean represent the business data in the transaction
    PaymentRequestBean bean = new PaymentRequestBean();
    bean.setCardNumber(new CardNumber(5432818929192L));
    bean.setAmount(new BigInteger("12"));
    bean.setAcquierID(1029);
    bean.setExtReference(937278626262L);
    bean.setCardTermId("ATM-10101");
    bean.setCardTermName("DUB87");
    bean.setMsisdn(353863579271L);
    bean.setCurrencyCode(840);
    bean.setOriginalData(999);

    // this map contains the technical/protocol fields
    final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    final DateFormat tf = new SimpleDateFormat("HH:mm:ss");
    Map<Integer, Object> params = new HashMap<Integer, Object>() {{
      put(3, 1010);
      put(7, df.parse("23-09-2015"));
      put(11, 618172);
      put(12, tf.parse("10:11:12"));
      put(13, df.parse("17-11-2016"));
    }};

    Message message = messages.createFromBean(MTI.create("0200"), bean);
    message.addFields(params);

    Assert.assertEquals(ListUtils.EMPTY_LIST, message.validate());

    String messageText = new String(messages.getMessageData(message));

    Assert.assertEquals(ExpectedBeanMessage, messageText);

    Message response = messages.duplicate(MTI.create("0400"), message);

    response.setFieldValue("currencyCode2", 885);
    response.setFieldValue("currencyCode3", 350);

    Assert.assertTrue(response.isValid());
  }

  @Test
  public void testParseBeanMessageAsMap()
      throws ParseException, IOException {
    Message message = messages.parse(ExpectedBeanMessage.getBytes());

    Assert.assertEquals(ListUtils.EMPTY_LIST, message.validate());
    Map<Integer, Object> result = message.getFields();

    Assert.assertEquals(ListUtils.EMPTY_LIST, message.validate());
    Assert.assertEquals(MTI.create("0200"), message.getMTI());

    Assert.assertEquals("5432*******92", result.get(2));
    Assert.assertEquals(new BigInteger("1010"), result.get(3));
    Assert.assertEquals(new BigInteger("12"), result.get(4));
    Assert.assertEquals("2000-09-23T00:00:00.000+01:00", result.get(7).toString());
    Assert.assertEquals(new BigInteger("618172"), result.get(11));
    Assert.assertEquals("10:11:12.000", result.get(12).toString());
    Assert.assertEquals("2000-11-17T00:00:00.000Z", result.get(13).toString());
    Assert.assertEquals(new BigInteger("1029"), result.get(32));
    Assert.assertEquals(new BigInteger("937278626262"), result.get(37));
    Assert.assertEquals("ATM-10101", result.get(41));
    Assert.assertEquals("DUB87", result.get(43));
    Assert.assertEquals(new BigInteger("353863579271"), result.get(48));
    Assert.assertEquals(new BigInteger("840"), result.get(49));
    Assert.assertEquals(new BigInteger("999"), result.get(90));
  }

  @Test
  public void testBitmap() {
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
		 */
    MessageTemplate template = messages.getTemplate(MTI.create("0400"));

    byte[] binaryBitmap = template.getBitmap().asBinary(Bitmap.Id.PRIMARY);
    Assert.assertEquals((byte) 0x42, binaryBitmap[0]);
    Assert.assertEquals((byte) 0x38, binaryBitmap[1]);
    Assert.assertEquals((byte) 0x00, binaryBitmap[2]);
    Assert.assertEquals((byte) 0x01, binaryBitmap[3]);
    Assert.assertEquals((byte) 0x08, binaryBitmap[4]);
    Assert.assertEquals((byte) 0xa1, binaryBitmap[5]);
    Assert.assertEquals((byte) 0x08, binaryBitmap[6]);
    Assert.assertEquals((byte) 0x04, binaryBitmap[7]);

    String hexBitmap = template.getBitmap().asHex(Bitmap.Id.PRIMARY);
    Assert.assertEquals("4238000108A10804", hexBitmap);
  }

  private static final String Payment_Request =
      "ISO01500007702007238000108A18000165264**********02305700000000032000"
          + "121022021393716600021312111181800601368034522937166CIB08520263     CIB-57357"
          + "HOSPITAL     CAIRO          EG01120167124377818";

  @Test
  public void testParseMessage()
      throws ParseException, IOException {
    Map<Integer, Object> params = messages.parse(Payment_Request.getBytes()).getFields();
    Assert.assertEquals("5264**********02", params.get(2));
    Assert.assertEquals(BigInteger.valueOf(305700), params.get(3));
    Assert.assertEquals(BigInteger.valueOf(32000), params.get(4));
    Assert.assertEquals("2000-12-10T22:02:13.000Z", params.get(7).toString());
    Assert.assertEquals(BigInteger.valueOf(937166), params.get(11));
    Assert.assertEquals("00:02:13.000", params.get(12).toString());
    Assert.assertEquals("2000-12-11T00:00:00.000Z", params.get(13).toString());
    Assert.assertEquals(BigInteger.valueOf(81800601368L), params.get(32));
    Assert.assertEquals(BigInteger.valueOf(34522937166L), params.get(37));
    Assert.assertEquals("CIB08520263", params.get(41));
    Assert.assertEquals("CIB-57357HOSPITAL     CAIRO          EG0", params.get(43));
    Assert.assertEquals(BigInteger.valueOf(20167124377L), params.get(48));
    Assert.assertEquals(BigInteger.valueOf(818), params.get(49));
  }

  private static final String ExpectMessage =
      "ISO0150000770200F238000108A180000000004000000000135432818929192"
          + "00101000000000120012121212006666661212001212041029937278626262"
          + "ATM-10101       DUB87                                   12353863579271840003C10";

  @Test
  public void
  testCreateMessageAPI()
      throws IOException, ParseException {
    Message request = messages.create(MTI.create(0x0200));
    Assert.assertFalse(request.isValid());

    Date testDate = (new SimpleDateFormat("ddMMyyyy:HHmmss")).parse("12122012:121200");
    request.setFieldValue(2, 5432818929192L);
    request.setFieldValue(3, 1010);
    request.setFieldValue(4, new BigInteger("1200"));
    request.setFieldValue(7, testDate);
    request.setFieldValue(11, 666666);
    request.setFieldValue(12, testDate);
    request.setFieldValue(13, testDate);
    request.setFieldValue(32, 1029);
    request.setFieldValue(37, 937278626262L);
    request.setFieldValue(41, "ATM-10101");
    request.setFieldValue(43, "DUB87");
    request.setFieldValue(48, 353863579271L);
    request.setFieldValue(49, 840);
    request.setFieldValue(90, BigInteger.TEN);

    Assert.assertTrue(request.isValid());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    messages.writeToStream(request, baos);
    Message message = messages.parse(new ByteArrayInputStream(baos.toByteArray()));
    Assert.assertEquals(ExpectMessage, new String(messages.getMessageData(message)));
  }

}
