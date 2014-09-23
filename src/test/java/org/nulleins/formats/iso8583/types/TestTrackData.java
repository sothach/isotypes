/**
 *
 */
package org.nulleins.formats.iso8583.types;

import org.junit.Assert;
import org.junit.Test;
import org.nulleins.formats.iso8583.TrackData;
import org.nulleins.formats.iso8583.formatters.TrackDataFormatter;
import org.nulleins.formats.iso8583.formatters.TypeFormatter;
import org.nulleins.formats.iso8583.types.CharEncoder;
import org.nulleins.formats.iso8583.types.Dimension;
import org.nulleins.formats.iso8583.types.FieldType;

import java.text.ParseException;

/**
 * @author phillipsr
 */
public class TestTrackData {
  private TypeFormatter<TrackData> formatter = new TrackDataFormatter(CharEncoder.ASCII);

  private static final byte[] TestDataT1 =
      "%B1234567890123445^EARIBUG/HUW.              ^99011200000000000000**XXX******?".getBytes();
  private static final byte[] TestDataT2 = "1234567890123456789=1015123".getBytes();
  private static final byte[] TestDataT3 =
      "011234567890123445=724724100000000000030300XXXX040400099010=************************==1=0000000000000000".getBytes();

  @Test
  public void testParseT1()
      throws ParseException {
    TrackData value = formatter.parse(FieldType.TRACKDATA, Dimension.parse("FIXED(50)"), 0, TestDataT1);

    Assert.assertTrue(value.getType() == TrackData.Track.TRACK1);
    Assert.assertEquals("EARIBUG", value.getName()[0]);
    Assert.assertEquals("HUW", value.getName()[1]);
    Assert.assertEquals(1234567890123445L, value.getPrimaryAccountNumber());
    Assert.assertEquals(9901, value.getExpirationDate());
    Assert.assertEquals(120, value.getServiceCode());
    Assert.assertEquals("0000000000000**XXX******", value.getDiscretionaryData());
  }

  @Test
  public void testParseT2()
      throws ParseException {
    TrackData value = formatter.parse(FieldType.TRACKDATA, Dimension.parse("FIXED(50)"), 0, TestDataT2);

    Assert.assertTrue(value.getType() == TrackData.Track.TRACK2);
    Assert.assertEquals(1234567890123456789L, value.getPrimaryAccountNumber());
    Assert.assertEquals(1015, value.getExpirationDate());
    Assert.assertEquals(123, value.getServiceCode());
    Assert.assertEquals("", value.getDiscretionaryData());
  }

  /**
   * Track3 data not supported
   * @throws ParseException
   */
  @Test(expected = ParseException.class)
  public void testParseT3()
      throws ParseException {

    TrackData value = formatter.parse(FieldType.TRACKDATA, Dimension.parse("FIXED(50)"), 0, TestDataT3);

    Assert.assertTrue(value.getType() == TrackData.Track.TRACK3);
  }

  @Test
  public void testFormatT1() {
    TrackData value = new TrackData(TrackData.Track.TRACK1);
    value.setName(new String[]{"EARIBUG", "HUW", "", ""});
    value.setPrimaryAccountNumber(1234567890123445L);
    value.setExpirationDate(9901);
    value.setServiceCode(120);
    value.setDiscretionaryData("0000000000000**XXX******");
    byte[] result = formatter.format(FieldType.TRACKDATA, value, Dimension.parse("LLVAR(80)"));
    Assert.assertEquals("B1234567890123445^EARIBUG/HUW.              99011200000000000000**XXX******", new String(result));
  }

  @Test
  public void testFormatT2() {
    TrackData value = new TrackData(TrackData.Track.TRACK2);
    value.setPrimaryAccountNumber(1234567890123456789L);
    value.setExpirationDate(1015);
    value.setServiceCode(123);
    value.setDiscretionaryData("");
    byte[] result = formatter.format(FieldType.TRACKDATA, value, Dimension.parse("LLVAR(80)"));
    Assert.assertEquals("1234567890123456789=1015123", new String(result));
  }

}
