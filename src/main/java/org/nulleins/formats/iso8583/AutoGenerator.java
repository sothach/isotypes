package org.nulleins.formats.iso8583;

/**
 * @author phillipsr
 */
public interface AutoGenerator {
  /**
   * @param autogen
   * @param field
   * @return
   */
  Object generate(String autogen, FieldTemplate field);

}
