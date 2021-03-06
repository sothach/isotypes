package org.nulleins.formats.iso8583;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 * Utility to generate a field value automatically, from a supplied autogen specification:
 * <table border='1'>
 * <tr><td><b>=now</b></td><td>Returns the current date time</td><td><font face="courier">java.util.Date</font></td></tr>
 * <tr><td><b>#<i>beanRef</i></b></td><td>Calls the <font face="courier">generate()</font> method on the referenced bean</td>
 * <td>(depends on the generator)</td></tr>
 * </table>
 * @author phillipsr
 */
@Component
public class AutoGeneratorFactory {
  @Resource
  private ApplicationContext context;

  /**
   * @param autogen
   * @param field
   * @return the specified auto-generated value, of null if specification not understood
   */

  public Object generate(final String autogen, final FieldTemplate field) {
    if (autogen == null || autogen.isEmpty()) {
      return null;
    }
    if ("=now".equals(autogen)) {
      return Calendar.getInstance().getTime();
    }
    if (!autogen.startsWith("#")) {
      return null;
    }
    AutoGenerator generator = context.getBean(autogen.substring(1), AutoGenerator.class);
    if (generator != null) {
      return generator.generate(autogen, field);
    }
    return null;
  }

}
