package org.nulleins.formats.iso8583;

import org.nulleins.formats.iso8583.formatters.TypeFormatter;
import org.nulleins.formats.iso8583.types.Bitmap;
import org.nulleins.formats.iso8583.types.BitmapType;
import org.nulleins.formats.iso8583.types.MTI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A message template is the definition of a specific ISO8583 message, defining its type,
 * content representation and the fields it can contain
 * @author phillipsr
 */
public class MessageTemplate {
  /** ID of the message template instance (used by Spring */
  private String id;
  /** name of the template (i.e., the message it represents) */
  private String name;
  /** header to be output at the start of a message (may be empty if none) */
  private String header;
  /** ISO8583 message type indicator for the message represented by this template */
  private MTI type;
  /** ISO8583 fields included in the target message */
  private Map<Integer, FieldTemplate> fields = new HashMap<>();
  /** mapping of logical names to field numbers */
  private final Map<String, Integer> nameIndex = new HashMap<>();
  /** bitmap indicating the fields present in the message */
  private final Bitmap bitmap = new Bitmap();
  /** schema to which this template belongs: provides default values, e.g., contentType */
  private MessageFactory schema;

  /**
   * Factory method to create a message template with the supplied properties
   * @param header     to be output at start of message
   * @param mti        message type indicator
   * @param bitmapType is a binary or hex bitmap to be used?
   * @return empty message template instance for the message type specified
   */
  public static MessageTemplate create(final String header, final MTI mti, final BitmapType bitmapType) {
    return new MessageTemplate(header, mti);
  }

  /** default constructor, used by Spring */
  MessageTemplate() {
  }

  /**
   * instantiate a message template with the supplied properties
   * @param header to be output at start of message
   * @param mti    message type indicator
   * @param header
   * @param mti
   */
  private MessageTemplate(final String header, final MTI mti) {
    this.header = header;
    type = mti;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getHeader() {
    return header != null ? header : schema.getHeader();
  }

  public void setHeader(final String header) {
    this.header = header;
  }

  public MTI getMessageTypeIndicator() {
    return type;
  }

  public void setMessageTypeIndicator(final MTI type) {
    this.type = type;
  }

  public String getType() {
    return type.toString();
  }

  public void setType(final String mti) {
    type = MTI.create(mti);
  }

  public Map<Integer, FieldTemplate> getFields() {
    return fields;
  }

  /**
   * Set the definition of the fields to be used in this message template,
   * and calculate the bitmap describing their presence or otherwise, for
   * all potential 192 fields (primary, secondary and tertiary bitmaps)
   * @param fields Field-number keyed map of field templates
   */
  public void setFields(final Map<Integer, FieldTemplate> fields) {
    this.fields = fields;
    bitmap.clear();
    for (Integer fieldNb : fields.keySet()) {
      bitmap.setField(fieldNb);
    }
    nameIndex.clear();
    for (FieldTemplate field : fields.values()) {
      nameIndex.put(field.getName(), field.getNumber());
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * Return a summary of this template, for logging/debugging usage
   */
  @Override
  public String toString() {
    return "MTI: " + type.describe()
        + " name: \"" + this.getName() + "\""
        + " header: [" + getHeader() + "]"
        + " #fields: " + this.fields.size();
  }

  /**
   * Add the supplied field template to the set of field in this message template,
   * updating the bitmap to reflect its presence
   * @param field
   */
  public void addField(final FieldTemplate field) {
    field.setMessage(this);
    fields.put(field.getNumber(), field);
    bitmap.setField(field.getNumber());
    // add the field to the name index, if set:
    String fieldName = field.getName();
    if (fieldName != null && !fieldName.isEmpty()) {
      nameIndex.put(field.getName(), field.getNumber());
    }
  }

  /** Answer with the bitmap for this message template */
  public Bitmap getBitmap() {
    return bitmap;
  }

  /**
   * Associate this template with the supplied schema
   * @param messageFactory to which this message template will belong
   */
  public void setSchema(final MessageFactory messageFactory) {
    this.schema = messageFactory;
  }

  /**
   * @param type
   * @return a formatter capable of formatting.parsing a field of <code>type</code>
   * @throws MessageException if not formatter registered for the supplied field type
   */
  TypeFormatter<?> getFormatter(final String type) {
    return schema.getFormatter(type);
  }

  /**
   * Does the supplied message conform to this template?
   * @param message instance to validate against this template
   * @return a list of errors detected, or an empty list, if message is valid
   */
  List<String> validate(final Message message) {
    List<String> result = new ArrayList<String>();
    if (!message.getMTI().equals(this.type)) {
      result.add("Message MTI (" + message.getMTI() + ") != Template MTI (" + type + ")");
    }
    if (!message.getHeader().equals(getHeader())) {
      result.add("Message header (" + message.getHeader() + ") != Template header (" + getHeader() + ")");
    }
    for (FieldTemplate field : fields.values()) {
      if (field.isOptional()) {
        continue;
      }
      Object msgField = message.getFields().get(field.getNumber());
      if (msgField == null) {
        result.add("Message field missing (" + field + ")");
      } else if (!field.validValue(msgField)) {
        result.add("Message field data invalid (" + msgField + ") for field: " + field);
      }
    }
    return result;
  }

  /**
   * Is the specified field present in this message?
   * @param fieldNumber
   * @return
   */
  boolean isFieldPresent(final int fieldNumber) {
    return bitmap.isFieldPresent(fieldNumber);
  }

  /**
   * Answer with the field template corresponding
   * to the supplied ,code>fieldNb</code>
   * @param fieldNb of field requested
   * @return the asscociated field template from this message template
   */
  public FieldTemplate getField(final int fieldNb) {
    return fields.get(fieldNb);
  }

  /**
   * Answer with the field number mapped to the supplied field name
   * @param fieldName to lookup
   * @return field number
   */
  int getFieldNumberForName(final String fieldName) {
    if (!nameIndex.containsKey(fieldName)) {
      throw new NoSuchFieldError(fieldName);
    }
    return nameIndex.get(fieldName);
  }

  public Collection<FieldTemplate> getNamedFields() {
    return fields.values();
  }
}
