package org.seefin.formats.iso8583.samples;

import org.junit.Assert;
import org.junit.Test;
import org.seefin.formats.iso8583.CardNumber;
import org.seefin.formats.iso8583.MessageException;
import org.seefin.formats.iso8583.PaymentRequestBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;

/**
 * @author phillipsr
 */
public class RunSamples {
  private static final String SampleContextPath = "classpath:org/seefin/formats/iso8583/samples/MessageSample-context.xml";

  /** Business-significant fields to include in message */
  private static final PaymentRequestBean Request = new PaymentRequestBean() {{
    setCardNumber(new CardNumber(12345678901234L));
    setAmount(BigInteger.TEN);
    setCurrencyCode(978);
    setMsisdn(353863447681L);
    setExtReference(1234);
    setCardTermId("ATM-1234");
    setCardTermName("BOI/ATM/D8/SJG");
    setOriginalData(0);
    setAcquierID(3031);
  }};


  /**
   * Call the <code>sendMessage</code> of the sample code;
   * test if successful if no exceptions thrown
   * @throws IOException
   * @throws ParseException
   */
  @Test
  public void runSamples()
      throws IOException, ParseException {
    ApplicationContext context = new ClassPathXmlApplicationContext(SampleContextPath);
    MessageSample sample = (MessageSample) context.getBean("messageSample");

    try {
      sample.sendMessage(0x0200, Request);
    } catch (MessageException e) {
      for (String line : e.getReasons()) {
        System.err.println(line);
      }
      e.printStackTrace();
      Assert.fail(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }
}
