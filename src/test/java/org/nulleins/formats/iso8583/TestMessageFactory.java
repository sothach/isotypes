package org.nulleins.formats.iso8583;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nulleins.formats.iso8583.FieldTemplate;
import org.nulleins.formats.iso8583.Message;
import org.nulleins.formats.iso8583.MessageFactory;
import org.nulleins.formats.iso8583.MessageTemplate;
import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.CharEncoder;
import org.nulleins.formats.iso8583.types.ContentType;
import org.nulleins.formats.iso8583.types.Dimension;
import org.nulleins.formats.iso8583.types.FieldType;
import org.nulleins.formats.iso8583.types.MTI;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;


/**
 * @author phillipsr
 */
public class TestMessageFactory {
  private static final MTI RequestMessage = MTI.create(0x0200);
  private MessageFactory factory;

  @Test
  public void testListSchema() {
    Set<MTI> schema = new HashSet<MTI>();
    for (MessageTemplate message : factory.getMessages()) {
      schema.add(message.getMessageTypeIndicator());
    }
    Assert.assertTrue(schema.contains(MTI.create(0x0200)));
  }

  @Test
  public void testCreateMessageFromTemplate() {
    Assert.assertEquals(
        MESSAGE_FACTORY_DESCRIPTION, factory.toString());

    Message message = factory.create(RequestMessage);
    Assert.assertEquals("Message mti=0200 header=ISO015000077 #field=0", message.toString());

    message.setFieldValue(2, BigInteger.TEN);
    Assert.assertEquals(BigInteger.TEN, message.getFieldValue(2));

    message.setFieldValue("TestField", BigInteger.ZERO);
    Assert.assertEquals(BigInteger.ZERO, message.getFieldValue("TestField"));

    message.removeField(2);
    message.setFieldValue(2, BigInteger.TEN);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateMessageNullField() {
    Message message = factory.create(RequestMessage);

    message.setFieldValue(2, null);
    message.setFieldValue("TestField", BigInteger.ZERO);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testBadCharset() {
    this.factory = new MessageFactory();
    factory.setCharset(new CharEncoder("tlhIngan-pIqaD")); // try to set Klingon charset
  }

  @Test(expected = NoSuchFieldError.class)
  public void missingSetFieldByNumberTest() {
    Message message = factory.create(RequestMessage);
    message.setFieldValue(3, BigInteger.TEN);
  }

  @Test(expected = NoSuchFieldError.class)
  public void missingSetFieldByNameTest() {
    Message message = factory.create(RequestMessage);
    message.setFieldValue("Frogmella", BigInteger.TEN);
  }

  @Test(expected = NoSuchFieldError.class)
  public void missingGetFieldByNumberTest() {
    Message message = factory.create(RequestMessage);
    message.getFieldValue(3);
  }

  @Test(expected = NoSuchFieldError.class)
  public void missingGetFieldByNameTest() {
    Message message = factory.create(RequestMessage);
    message.getFieldValue("Frogmella");
  }

  @Test(expected = IllegalArgumentException.class)
  public void fieldNumberInvalidData() {
    Message message = factory.create(RequestMessage);
    message.setFieldValue(2, 1.2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void fieldNameInvalidData() {
    Message message = factory.create(RequestMessage);
    message.setFieldValue("TestField", 1.2);
  }

  @Test(expected = NoSuchFieldError.class)
  public void missingRemoveFieldByNumberTest() {
    Message message = factory.create(RequestMessage);
    message.removeField(3);
  }

  private static final String MESSAGE_FACTORY_DESCRIPTION =
      "MessageFactory id=testFactory description='Test Message Schema' "
          + "header=ISO015000077 contentType=TEXT charset=US-ASCII bitmapType=HEX messages# 1";

  @Before
  public void createFactory() {
    this.factory = new MessageFactory();
    factory.setId("testFactory");
    factory.setDescription("Test Message Schema");
    factory.setBitmapType(BitmapType.HEX);
    factory.setContentType(ContentType.TEXT);
    factory.setHeader("ISO015000077");
    MessageTemplate template = MessageTemplate.create("ISO015000077", RequestMessage, BitmapType.HEX);
    template.addField(new FieldTemplate(2, FieldType.NUMERIC, Dimension.parse("fixed(6)"), "TestField", ""));
    factory.addMessage(template);
    factory.initialize();
  }
}
