package org.seefin.formats.iso8583;

import org.seefin.formats.iso8583.types.Dimension;
import org.seefin.formats.iso8583.types.MTI;

import java.text.ParseException;
import java.text.ParsePosition;


/**
 * Definition of an ISO8583 message field, capable for formatting and parsing message
 * fields, based upon its configuration
 * <p/>
 * Example:</br>
 * <code>
 * &lt;field number="2" type="LLVAR" name="CardNumber" description="Payment Card Number" /&gt;
 * </code>
 * @author phillipsr
 */
public class FieldTemplate {
  private String type;
  private Dimension dimension;
  private int number;
  private String name;
  private String description;
  private String defaultValue;
  private String autogenSpec;
  private boolean optional;
  private MTI messageType;
  private MessageTemplate message;

  public FieldTemplate(int number, String type, Dimension dimension, String name, String description) {
    this.number = number;
    this.type = type;
    setDimension(dimension);
    this.name = name;
    this.description = description;
  }

  public FieldTemplate() {
  }

  public int getNumber() {
    return number;
  }

  public String getMessageType() {
    return messageType.toString();
  }

  public void setMessageType(String type) {
    this.messageType = MTI.create(type);
  }

  public String getType() {
    return type.toString();
  }

  public void setType(String type) {
    this.type = type;
  }

  public Dimension getDimension() {
    return dimension;
  }

  public void setDimension(Dimension dimension) {
    this.dimension = dimension;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getAutogen() {
    return autogenSpec;
  }

  public void setAutogen(String autogenSpec) {
    this.autogenSpec = autogenSpec;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public boolean isOptional() {
    return optional;
  }

  public void setOptional(boolean optional) {
    this.optional = optional;
  }

  /**
   * Use this field definition to format the data supplied
   * @param fieldValue
   * @return data
   * @throws MessageException if the formatter failed to create a field of the correct size
   */
  public byte[]
  format(Object value) {
    String result = null;
    try {
      result = new String(
          message.getFormatter(type).format(type, value, this.dimension));
    } catch (Exception e) {
      throw new IllegalStateException("Could not format data [" + value + "] for field " + this, e);
    }

    if (this.dimension.getType() == Dimension.Type.FIXED && result.length() != dimension.getLength()) {
      throw new MessageException(this + ": Formatter did not format fixed field to specified length, value=[" + result + "]");
    }
    if (this.dimension.getType() == Dimension.Type.VARIABLE && result.length() > dimension.getLength() + dimension.getVSize()) {
      throw new MessageException(this + ": Formatter exceeded maximum length for variable field; value=[" + result + "]");
    }
    return result.getBytes();
  }

  @Override
  public String toString() {
    return "Field nb=" + this.getNumber()
        + " name=" + this.getName()
        + " type=" + this.getType()
        + " dim=" + dimension
        + (autogenSpec != null ? (" autogen=[" + autogenSpec + "]") : "")
        + (defaultValue != null ? (" default=[" + defaultValue + "]") : "");
  }

  public Object parse(byte[] data, ParsePosition pos)
      throws ParseException {
    return message.getFormatter(type).parse(type, dimension, data.length, data);
  }

  /**
   * @param messageTemplate
   */
  public void setMessage(MessageTemplate messageTemplate) {
    this.message = messageTemplate;
  }

  /**
   * @param value
   * @return
   */
  public boolean validValue(Object value) {
    return message.getFormatter(type).isValid(value, type, dimension);
  }

}
