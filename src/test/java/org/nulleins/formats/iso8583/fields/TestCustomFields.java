package org.nulleins.formats.iso8583.fields;

import junit.framework.Assert;
import org.junit.Test;
import org.nulleins.formats.iso8583.formatters.AddAmountsFormatter;
import org.nulleins.formats.iso8583.formatters.CardAcceptorLocationFormatter;
import org.nulleins.formats.iso8583.types.PostilionAddAmount;
import org.nulleins.formats.iso8583.types.CardAcceptorLocation;
import org.nulleins.formats.iso8583.types.Dimension;

import java.math.BigInteger;
import java.text.ParseException;

/**
 * @author phillipsr
 */
public class TestCustomFields {
  private static final CardAcceptorLocationFormatter calFormatter = new CardAcceptorLocationFormatter();
  private static final AddAmountsFormatter aaFormatter = new AddAmountsFormatter();

  @Test
  public void testCustomerFormat() {
    CardAcceptorLocation target = new CardAcceptorLocation("PH Rumukrushi", "Porthar", "PH", "NG");

    byte[] data = calFormatter.format("CALf", target, Dimension.parse("FIXED(40)"));
    Assert.assertEquals("PH Rumukrushi          Porthar      PHNG", new String(data));
  }

  @Test
  public void testCustomerParse()
      throws ParseException {
    byte[] data = "PH Rumukrushi          Porthar      PHNG".getBytes();
    CardAcceptorLocation response = calFormatter.parse("CALf", Dimension.parse("FIXED(40)"), 40, data);
    Assert.assertEquals("PH Rumukrushi", response.getLocation());
    Assert.assertEquals("Porthar", response.getCity());
    Assert.assertEquals("PH", response.getState());
    Assert.assertEquals("NG", response.getCountry());
  }

  @Test
  public void testCustomerFormat2() {
    PostilionAddAmount[] target = new PostilionAddAmount[3];
    target[0] = new PostilionAddAmount(10, 2, 566, new BigInteger("2426026"));
    target[1] = new PostilionAddAmount(10, 3, 566, BigInteger.ZERO);
    target[2] = new PostilionAddAmount(10, 1, 566, new BigInteger("2426026"));
    byte[] data = aaFormatter.format("AAf", target, Dimension.parse("lllvar(10)"));
    Assert.assertTrue(data.length == 60);
    Assert.assertEquals("1002566C0000024260261003566C0000000000001001566C000002426026", new String(data));
  }

  @Test
  public void testCustomerParse2()
      throws ParseException {
    //             | . .  .            | . .  .            | . .  .
    byte[] data = "1002566C0000024260261003566C0000000000001001566D000002426026".getBytes();
    PostilionAddAmount[] response = aaFormatter.parse("AAf", Dimension.parse("FIXED(40)"), data.length, data);

    Assert.assertTrue(response.length == 3);

    Assert.assertEquals((Integer) 10, response[0].getAccountType());
    Assert.assertEquals(BigInteger.valueOf(2426026), response[0].getAmount());
    Assert.assertEquals((Integer) 2, response[0].getAmountType());
    Assert.assertEquals((Integer) 566, response[0].getCurrencyCode());

    Assert.assertEquals((Integer) 10, response[1].getAccountType());
    Assert.assertEquals(BigInteger.ZERO, response[1].getAmount());
    Assert.assertEquals((Integer) 3, response[1].getAmountType());
    Assert.assertEquals((Integer) 566, response[1].getCurrencyCode());

    Assert.assertEquals((Integer) 10, response[2].getAccountType());
    Assert.assertEquals(BigInteger.valueOf(2426026).negate(), response[2].getAmount());
    Assert.assertEquals((Integer) 1, response[2].getAmountType());
    Assert.assertEquals((Integer) 566, response[2].getCurrencyCode());
  }

  @Test
  public void twoWayTest()
      throws ParseException {
    String data = "1002566C0000024260261003566C0000000000001001566C000002426026";
    PostilionAddAmount[] target = aaFormatter.parse("AAf", Dimension.parse("LLVAR(120)"), data.length(), data.getBytes());
    String response = new String(aaFormatter.format("AAf", target, Dimension.parse("LLVAR(120)")));
    Assert.assertEquals(data, response);
  }

}
