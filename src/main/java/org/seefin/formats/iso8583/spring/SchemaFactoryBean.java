package org.seefin.formats.iso8583.spring;

import org.seefin.formats.iso8583.FieldTemplate;
import org.seefin.formats.iso8583.MessageFactory;
import org.seefin.formats.iso8583.MessageTemplate;
import org.seefin.formats.iso8583.formatters.TypeFormatter;
import org.springframework.beans.factory.FactoryBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean factory to create a MessageFactory instance from the parsed XML definition
 * @author phillipsr
 */
public class SchemaFactoryBean
    implements FactoryBean<MessageFactory> {
  private String id;
  private MessageFactory schema;
  private Map<String, MessageTemplate> messages = new HashMap<String, MessageTemplate>();
  /* map of field lists by message MTI */
  private Map<String, List<FieldTemplate>> fields = new HashMap<String, List<FieldTemplate>>();
  private String description;
  private List<FormatterSpec> formatters = new ArrayList<FormatterSpec>();

  /**
   * Set the message object that will be decorated and
   * returned by this bean factory
   * @param process the top-level process object defined
   */
  public void
  setSchema(MessageFactory schema) {
    this.schema = schema;
  }

  public void
  setId(String id) {
    this.id = id;
  }

  public void setMessages(List<MessageTemplate> messageList) {
    for (MessageTemplate message : messageList) {
      messages.put(message.getType(), message);
    }
  }

  public void setFormatters(List<FormatterSpec> formatterList) {
    for (FormatterSpec formatter : formatterList) {
      formatters.add(formatter);
    }
  }

  public void setFields(List<FieldTemplate> fieldList) {
    for (FieldTemplate field : fieldList) {
      List<FieldTemplate> messageFields = fields.get(field.getMessageType());
      if (messageFields == null) {
        messageFields = new ArrayList<FieldTemplate>();
        fields.put(field.getMessageType(), messageFields);
      }
      messageFields.add(field);
    }
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Answer with the messageSet object, with its state fields assigned from the
   * values previously set in the factory, providing the required type conversion
   */
  @Override
  public MessageFactory getObject()
      throws Exception {
    if (id != null) {
      schema.setId(id);
    }
    if (description != null) {
      schema.setDescription(description);
    }
    if (this.formatters != null) {
      for (FormatterSpec item : formatters) {
        addFormatter(item);
      }
    }
    if (this.messages != null) {
      for (Map.Entry<String, MessageTemplate> item : messages.entrySet()) {
        MessageTemplate message = item.getValue();
        setMessageFields(item.getKey(), message);
        schema.addMessage(message);
      }
    }
    schema.initialize();
    return schema;
  }

  private void addFormatter(FormatterSpec formatter)
      throws InstantiationException, IllegalAccessException {
    Object fmtr = formatter.getSpec();
    if (java.lang.Class.class.isInstance(fmtr)) { // action specifies a class to be instantiated
      Class<?> fmtrClass = (Class<?>) fmtr;
      TypeFormatter<?> fmtrAction = (TypeFormatter<?>) fmtrClass.newInstance();
      schema.addFormatter(formatter.getType(), fmtrAction);
    } else { // formatter is either a bean reference or class name
      schema.addFormatter(formatter.getType(), (TypeFormatter<?>) fmtr);
    }
  }

  private void setMessageFields(String type, MessageTemplate message) {
    List<FieldTemplate> fields = this.fields.get(type);
    for (FieldTemplate field : fields) {
      if (message.getFields().containsKey(field.getName())) {
        throw new IllegalStateException("duplicate field name: "
            + field.getName() + " defined for Message type "
            + message.getMessageTypeIndicator());
      }
      field.setMessage(message);
      message.addField(field);
    }
    ;
  }

  /** Answer with the target object type that this factory will create */
  @Override
  public Class<?> getObjectType() {
    return MessageFactory.class;
  }

  /** Should Spring treat the constructed object as a singleton? */
  @Override
  public boolean isSingleton() {
    return true;
  }
}