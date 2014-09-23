package org.seefin.formats.iso8583.samples;

import org.seefin.formats.iso8583.Message;
import org.seefin.formats.iso8583.MessageException;
import org.seefin.formats.iso8583.MessageFactory;
import org.seefin.formats.iso8583.PaymentRequestBean;
import org.seefin.formats.iso8583.TrackData;
import org.seefin.formats.iso8583.TrackData.Track;
import org.seefin.formats.iso8583.types.MTI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Sample class demonstrating the isotypes API
 * <p/>
 * @author phillipsr
 */
@Component
public class MessageSample {
  @Resource
  private MessageFactory factory; // defined in the iso8583.xml context
  private OutputStream output = new OutputStream() {
    @Override
    public void write(int b) throws IOException { /* throw it all away */ }
  };

  public void sendMessage(int mti, PaymentRequestBean request)
      throws IOException, ParseException {
    // instantiate request from business object
    Message message = factory.createFromBean(MTI.create(mti), request);

    // add fields used by the ISO8583 protocol/server
    Date dateTime = (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).parse("01-01-2013 10:15:30");
    message.setFieldValue(3, 101010);     // processing code
    message.setFieldValue(7, dateTime);  // transmission date and time
    message.setFieldValue(11, 4321);     // trace (correlation) number
    message.setFieldValue(12, dateTime); // transaction time
    message.setFieldValue(13, dateTime); // transaction date

    TrackData track1data = new TrackData(Track.TRACK1);
    track1data.setPrimaryAccountNumber(123456789L);
    track1data.setName(new String[]{"Bugg", "Harry", "H", "Mr"});
    track1data.setExpirationDate(1212);
    track1data.setServiceCode(120);
    message.setFieldValue(45, track1data); // transaction date
    TrackData track2data = new TrackData(Track.TRACK2);
    track2data.setPrimaryAccountNumber(track1data.getPrimaryAccountNumber());
    track2data.setExpirationDate(track1data.getExpirationDate());
    track2data.setServiceCode(track1data.getServiceCode());
    message.setFieldValue(35, track2data); // transaction date

    // log the message content:
    for (String line : message.describe()) {
      System.out.println("INFO: " + line);
    }

    // check the message is good-to-go:
    List<String> errors = message.validate();
    if (!errors.isEmpty()) {
      throw new MessageException(errors);
    }

    // write it to the dummy output stream:
    factory.writeToStream(message, output);
  }

}
