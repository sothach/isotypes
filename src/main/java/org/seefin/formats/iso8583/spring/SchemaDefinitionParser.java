package org.seefin.formats.iso8583.spring;

import org.seefin.formats.iso8583.FieldTemplate;
import org.seefin.formats.iso8583.MessageFactory;
import org.seefin.formats.iso8583.MessageTemplate;
import org.seefin.formats.iso8583.types.BitmapType;
import org.seefin.formats.iso8583.types.ContentType;
import org.seefin.formats.iso8583.types.Dimension;
import org.seefin.formats.iso8583.types.MTI;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Spring bean definition parser to parse ISO message schema
 * @author phillipsr
 */
public class SchemaDefinitionParser
    extends AbstractBeanDefinitionParser {
  @Override
  protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
    BeanDefinitionBuilder factory
        = BeanDefinitionBuilder.rootBeanDefinition(SchemaFactoryBean.class);
    AbstractBeanDefinition messageSet = parseMessageSet(element);
    factory.addPropertyValue("schema", messageSet);

    Element desc = DomUtils.getChildElementByTagName(element, "description");
    if (desc != null) {
      factory.addPropertyValue("description", desc.getTextContent().trim());
    }

    Element formatters = DomUtils.getChildElementByTagName(element, "formatters");
    if (formatters != null) {
      List<Element> formatterList = DomUtils.getChildElementsByTagName(formatters, "formatter");
      parseFormatters(formatterList, factory);
    }

    List<Element> messages = DomUtils.getChildElementsByTagName(element, "message");
    if (messages != null && messages.size() > 0) {
      parseMessages(messages, factory);
    }
    return factory.getBeanDefinition();
  }

  private static AbstractBeanDefinition parseMessageSet(Element element) {
    BeanDefinitionBuilder messageSet
        = BeanDefinitionBuilder.rootBeanDefinition(MessageFactory.class);
    messageSet.addPropertyValue("id", element.getAttribute("id"));
    messageSet.addPropertyValue("header", element.getAttribute("header"));
    messageSet.addPropertyValue("strict", element.getAttribute("strict"));
    BitmapType bitmapType = BitmapType.valueOf(element.getAttribute("bitmapType").trim().toUpperCase());
    messageSet.addPropertyValue("bitmapType", bitmapType.toString());
    ContentType contentType = ContentType.valueOf(element.getAttribute("contentType").trim().toUpperCase());
    messageSet.addPropertyValue("contentType", contentType.toString());
    String charset = element.getAttribute("charset").trim().toUpperCase();
    messageSet.addPropertyValue("charset", charset);

    return messageSet.getBeanDefinition();
  }

  private static void parseMessages(List<Element> messages, BeanDefinitionBuilder factory) {
    ManagedList<AbstractBeanDefinition> messageList
        = new ManagedList<AbstractBeanDefinition>(messages.size());
    ManagedList<AbstractBeanDefinition> allFields = new ManagedList<AbstractBeanDefinition>();
    for (int i = 0; i < messages.size(); ++i) {
      Element messageElement = messages.get(i);
      MTI type = MTI.create(messageElement.getAttribute("type").trim());
      AbstractBeanDefinition message = parseMessage(type, messageElement);
      messageList.add(message);
      List<Element> fields = DomUtils.getChildElementsByTagName(messageElement, "field");
      if (fields == null || fields.size() == 0) {
        continue;
      }
      for (Element element : fields) {
        allFields.add(parseField(type, element));
      }
      factory.addPropertyValue("fields", allFields);
    }
    factory.addPropertyValue("messages", messageList);
  }

  private static AbstractBeanDefinition parseMessage(MTI type, Element element) {
    BeanDefinitionBuilder result
        = BeanDefinitionBuilder.rootBeanDefinition(MessageTemplate.class);
    result.addPropertyValue("type", type.toString());
    result.addPropertyValue("name", element.getAttribute("name"));
    return result.getBeanDefinition();
  }

  private static AbstractBeanDefinition parseField(MTI type, Element element) {
    BeanDefinitionBuilder result
        = BeanDefinitionBuilder.rootBeanDefinition(FieldTemplate.class);

    result.addPropertyValue("messageType", type.toString());
    String defaultValue = element.getTextContent();
    if (defaultValue != null && defaultValue.isEmpty() == false) {
      result.addPropertyValue("defaultValue", defaultValue);
    }
    result.addPropertyValue("number", element.getAttribute("f"));
    result.addPropertyValue("type", element.getAttribute("type"));
    result.addPropertyValue("autogen", element.getAttribute("autogen"));
    String dim = element.getAttribute("dim");
    if (dim != null && dim.isEmpty() == false) {
      result.addPropertyValue("dimension", Dimension.parse(dim));
    }
    result.addPropertyValue("optional", element.getAttribute("optional"));
    result.addPropertyValue("name", element.getAttribute("name"));
    result.addPropertyValue("description", element.getAttribute("desc"));

    return result.getBeanDefinition();
  }

  private static void parseFormatters(List<Element> formatters,
                  BeanDefinitionBuilder factory) {
    ManagedList<AbstractBeanDefinition> formattersList
        = new ManagedList<AbstractBeanDefinition>(formatters.size());
    for (Element element : formatters) {
      formattersList.add(parseFormatter(element));
    }
    factory.addPropertyValue("formatters", formattersList);
  }

  private static AbstractBeanDefinition parseFormatter(Element element) {
    BeanDefinitionBuilder result
        = BeanDefinitionBuilder.rootBeanDefinition(FormatterSpec.class);
    result.addPropertyValue("type", element.getAttribute("type"));

    String classAttr = element.getAttribute("class");
    String refAttr = element.getAttribute("ref");
    if (classAttr.isEmpty() == false && refAttr.isEmpty() == false) {
      throw new IllegalStateException("Cannot specify both class and ref for a formatter: choose one! ");
    }
    boolean useRef = refAttr.isEmpty() == false;
    result.addPropertyValue("spec", useRef ? refAttr : classAttr);

    setFormatter(element, result);
    return result.getBeanDefinition();
  }

  private static void setFormatter(Element nodeElement, BeanDefinitionBuilder formatter) {
    String formatterName = nodeElement.getAttribute("class");
    // check is 'action=class-name' is specified
    if (formatterName != null && formatterName.isEmpty() == false) { // formatter class specified, check that it is a valid class name
      try {
        Class<?> formatterClass = SchemaDefinitionParser.class.getClassLoader().loadClass(formatterName);
        formatter.addPropertyValue("spec", formatterClass);
      } catch (Exception e) {
        throw new IllegalStateException("could load action class for action: " + formatterName, e);
      }
      return;
    }
    formatterName = nodeElement.getAttribute("ref");
    if (formatterName != null && formatterName.isEmpty() == false) { // set the formatter as a property reference
      formatter.addPropertyReference("spec", nodeElement.getAttribute("ref"));
    }
    return;
  }

}