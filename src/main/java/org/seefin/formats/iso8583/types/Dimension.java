package org.seefin.formats.iso8583.types;

import org.seefin.formats.iso8583.MessageException;

/**
 * Specification of the dimension of an ISO8583 field: fields can
 * be of fixed or variable length (hollerithian)
 * @author phillipsr
 */
public final class Dimension {
  public enum Type {
    FIXED,
    VARIABLE;
  }

  private final Type type;
  private final int vSize;
  private final int length;

  /**
   * Create a specification for a field with the supplied
   * parameters
   * @param type   of the field: variable or fixed
   * @param vSize  size of the field-length specifier
   * @param length of the field (max length if a variable field)
   */
  public Dimension(Type type, int vSize, int length) {
    this.type = type;
    this.vSize = vSize;
    this.length = length;
  }

  public Type getType() {
    return type;
  }

  /**
   * Answer with the variable size specifier size
   * @return the size of the variable field's size specifier
   * @throws MessageException if the field is not a variable-sized field
   */
  public int getVSize() {
    if (type != Type.VARIABLE) {
      throw new MessageException("Variable size not valid for non-variable field");
    }
    return vSize;
  }

  public int getLength() {
    return length;
  }

  @Override
  public String
  toString() {
    return type == Type.FIXED ?
        String.format("FIXED(%3d)", length) :
        String.format("VAR%d (%3d)", vSize, length);
  }

  /**
   * Parse a field dimension specification, e.g.,<br/>
   * <code>llvar(40)</code>: variable field with 2 digit size specifier and maximum length of 40, or
   * <code>fixed(6)</code> a field 6 bytes long
   * @param value
   * @return
   * @throws IllegalArgumentException if the value is null or does not contain a valid dimension specification
   */
  public static Dimension
  parse(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Dimension value cannot be null");
    }
    int pos = value.indexOf('(');
    if (pos == -1) {
      throw new IllegalArgumentException("Dimension value must contain size: e.g., fixed(12)");
    }
    int vSize = 0;
    String[] dim = value.split("\\(");
    pos = dim[1].indexOf(')');
    if (pos == -1) {
      throw new IllegalArgumentException("Dimension value must contain size: e.g., fixed(12)");
    }
    String lengthPart = dim[1].substring(0, pos).trim();
    int length = Integer.parseInt(lengthPart);
    String typeValue = dim[0].trim().toUpperCase();
    if ("FIXED".equals(typeValue)) {
      return new Dimension(Type.FIXED, vSize, length);
    }

    if ("LVAR".equals(typeValue)) {
      vSize = 1;
    } else if ("LLVAR".equals(typeValue)) {
      vSize = 2;
    } else if ("LLLVAR".equals(typeValue)) {
      vSize = 3;
    } else {
      throw new IllegalArgumentException("Dimension type must be one of: fixed, lvar, llvar, lllvar");
    }

    return new Dimension(Type.VARIABLE, vSize, length);
  }
}
