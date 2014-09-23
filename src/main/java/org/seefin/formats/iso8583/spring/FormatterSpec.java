package org.seefin.formats.iso8583.spring;

/**
 * Specification holder for data formatter during parsing,
 * will be used to instantiate or reference the appropriate
 * formatter bean when the MessageFactory is itself created
 * @author phillipsr
 */
public class FormatterSpec {
  private String type; // the type that this formatter will format (equates to the <field> type attribute)
  private Object spec; // either the full name of the formatter class, or a reference to a bean

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Object getSpec() {
    return spec;
  }

  public void setSpec(final Object spec) {
    this.spec = spec;
  }

  @Override
  public String toString() {
    return "Formatter: type=(" + type + ") class=[" + spec + "]";
  }
}
