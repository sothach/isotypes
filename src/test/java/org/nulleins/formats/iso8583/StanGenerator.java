package org.nulleins.formats.iso8583;

import org.nulleins.formats.iso8583.AutoGenerator;
import org.nulleins.formats.iso8583.FieldTemplate;
import org.springframework.stereotype.Component;

/**
 * @author phillipsr
 */
@Component
public class StanGenerator
    implements AutoGenerator {
  private final Integer floor;
  private final Integer ceiling;
  private final ThreadLocal<Integer> next = new ThreadLocal<Integer>();

  public StanGenerator(Integer floor, Integer ceiling) {
    this.floor = floor;
    this.ceiling = ceiling;
    next.set(floor);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer generate(String autogen, FieldTemplate field) {
    Integer result = next.get();
    next.set(result + 1 > ceiling ? floor : result + 1);
    return result;
  }

}
