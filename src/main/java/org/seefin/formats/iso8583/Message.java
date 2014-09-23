package org.seefin.formats.iso8583;

import org.apache.commons.collections.ListUtils;
import org.seefin.formats.iso8583.types.MTI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An ISO8583 message instance, being a number of header values and a set
 * of field values
 * <p/>
 * Every message has a <code>template</code> that describes the message and its content
 * @author phillipsr
 */
public class Message {
  private final MTI messageTypeIndicator;
  private String header;
  private Map<Integer, Object> fields = new HashMap<Integer, Object>();
  private MessageTemplate template;

  /**
   * Instantiate a new message, of the type specified
   * @param messageTypeIndicator
   * @throws IllegalArgumentException if the supplied MTI is null
   */
  public Message(MTI messageTypeIndicator) {
    if (messageTypeIndicator == null) {
      throw new IllegalArgumentException("MTI cannot be null");
    }
    this.messageTypeIndicator = messageTypeIndicator;
  }

  /** Answer with this message's MTI */
  public MTI getMTI() {
    return messageTypeIndicator;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public Map<Integer, Object> getFields() {
    return fields;
  }

  public void setFields(Map<Integer, Object> fields) {
    this.fields = fields;
  }

  public Map<String, Object> getNamedFields() {
    Map<String, Object> result = new HashMap<String, Object>(fields.size());
    for (Map.Entry<Integer, Object> item : fields.entrySet()) {
      FieldTemplate field = template.getField(item.getKey());
      result.put(field.getName(), item.getValue());
    }
    return result;
  }

  /**
   * Set the value of the field specified
   * @param fieldNumber of the field to receive the value
   * @param value       object to set
   * @throws NoSuchFieldError         if the field is not defined for this message,
   * @throws IllegalArgumentException if the value data type supplied is not
   *                                  compatible with the defined field type
   */
  public void setFieldValue(int fieldNumber, Object value) {
    if (template.isFieldPresent(fieldNumber) == false) {
      throw new NoSuchFieldError(fieldNumber + "");
    }
    FieldTemplate field = template.getFields().get(fieldNumber);
    if (field.validValue(value) == false) {
      throw new IllegalArgumentException("Supplied value (" + value + ") not valid for field:" + field);
    }
    fields.put(fieldNumber, value);
  }

  /**
   * Set the value of the named field
   * @param fieldName the field to receive the value
   * @param value     object to set
   * @throws NoSuchFieldError         if the field is not defined for this message
   * @throws IllegalArgumentException if the value data type supplied is not
   *                                  compatible with the defined field type
   */
  public void setFieldValue(String fieldName, Object value) {
    setFieldValue(
        template.getFieldNumberForName(fieldName), value);
  }

  /**
   * Answer with the value of the field specified
   * @param fieldNumber of field whose value is requested
   * @return the field value
   * @throws NoSuchFieldError if the field is not defined for this message
   */
  public Object getFieldValue(int fieldNumber) {
    if (template.isFieldPresent(fieldNumber) == false) {
      throw new NoSuchFieldError(fieldNumber + "");
    }
    return fields.get(fieldNumber);
  }

  /**
   * Answer with the value of the field specified
   * @param fieldName of field whose value is requested
   * @return the field value
   * @throws NoSuchFieldError if the field is not defined for this message
   */
  public Object getFieldValue(String fieldName) {
    return getFieldValue(
        template.getFieldNumberForName(fieldName));
  }

  /**
   * Remove the field specified from this message's field set
   * @param fieldNumber
   * @throws NoSuchFieldError if the field is not defined for this message
   */
  public void removeField(int fieldNumber) {
    if (template.isFieldPresent(fieldNumber) == false) {
      throw new NoSuchFieldError(fieldNumber + "");
    }
    fields.remove(fieldNumber);
  }

  /**
   * Answer with the empty list if this message is
   * valid according to its template, otherwise return
   * a list of error messages
   * @return
   */
  public List<String> validate() {
    return template.validate(this);
  }

  /**
   * Set the message template that defines this message instance
   * @param messageTemplate
   */
  public void setTemplate(MessageTemplate messageTemplate) {
    this.template = messageTemplate;
  }

  /** return a summary of this field, for logging purposes */
  @Override
  public String toString() {
    return "Message mti=" + messageTypeIndicator + " header=" + header + " #field=" + fields.size();
  }

  /**
   * Return an iterator to iterate over the multi-line
   * description of this message, including message type
   * information, field type information and field values
   * @return
   */
  public Iterable<String> describe() {
    return new Describer(template, fields);
  }

  /**
   * Add all the supplied field values to this message
   * @param fieldValues
   */
  public void addFields(Map<Integer, Object> fieldValues) {
    fields.putAll(fieldValues);
  }

  /**
   * Is this message valid, according to it's template?
   * @return true if all the required fields are present
   */
  public boolean isValid() {
    return ListUtils.EMPTY_LIST.equals(this.validate());
  }

  /**
   * is field <code>number</code> present in the message?
   * @param number
   * @return
   */
  public boolean isFieldPresent(int number) {
    return template.isFieldPresent(number);
  }

}
