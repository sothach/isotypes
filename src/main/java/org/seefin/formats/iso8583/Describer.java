/**
 *
 */
package org.seefin.formats.iso8583;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Utility class that can describe an ISO8583 message in readable form, used for
 * logging and debugging purposes
 * <p/>
 * A Describer provides its message description as an Iterable, to make it easy to
 * output multiple line descriptions in log files, UI, etc.
 * @author phillipsr
 */
class Describer
    implements Iterable<String> {
  protected final MessageTemplate template;
  protected final Map<Integer, Object> fields;

  public Describer(final MessageTemplate template, final Map<Integer, Object> fields) {
    this.template = template;
    this.fields = fields;
  }

  @Override
  public String
  toString() { final StringBuilder result = new StringBuilder();
    for (final String line : this) {
      result.append(line).append("\n");
    }
    return result.toString();
  }

  @Override
  public Iterator<String> iterator() {
    return new Iterator<String>() {
      private boolean more = true;
      private final Integer[] fieldkeys = getKeys(fields);
      private int i;

      /** is there any more description? */
      @Override
      public boolean hasNext() {
        return more;
      }

      private Integer[]
      getKeys(final Map<Integer, Object> fields) {
        if (fields == null) {
          return new Integer[0];
        }
        final Set<Integer> integers = fields.keySet();
        final Integer[] result = integers.toArray(new Integer[integers.size()]);
        Arrays.sort(result);
        return result;
      }

      /**
       * Return the next formatted line of description
       */
      @Override
      public String next() {
        if (i == 0) {
          i++;
          more = fieldkeys.length > 0;
          return template.toString();
        }
        if (i == 1) {
          i++;
          return String.format("%3.3s: %-15.15s %-37.37s %-15.15s  %s",
              "F#", "Dimension:Type", "Value (is-a)", "Name", "Description");
        }
        more = i <= fieldkeys.length;
        final int key = fieldkeys[i - 2];
        final FieldTemplate field = template.getFields().get(key);
        final Object value = fields.get(key);
        i++;
        return String.format("%3d: %s:%-4s %-37s %-15s %s",
            key, field.getDimension(), field.getType(), "[" + value + "] ("
                + (value != null ? value.getClass().getSimpleName() : "") + ")",
            field.getName(), field.getDescription());
      }

      @Override
      public void remove() {
        throw new NoSuchMethodError("'remove' not implemented");
      }
    };
  }
}
