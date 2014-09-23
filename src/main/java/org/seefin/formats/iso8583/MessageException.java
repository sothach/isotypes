package org.seefin.formats.iso8583;

import java.util.ArrayList;
import java.util.List;

/**
 * Instance of an error encountered processing an ISO8583 message
 * @author phillipsr
 */
public class MessageException
    extends RuntimeException {
  private List<String> reasons = new ArrayList<String>();

  public MessageException(List<String> reasons) {
    this.reasons = reasons;
  }

  public MessageException(Throwable cause) {
    super(cause);
    reasons.add(cause.getMessage());
  }

  public MessageException(String message) {
    super(message);
    reasons.add(message);
  }

  public MessageException(String message, Throwable cause) {
    super(message, cause);
    reasons.add(message);
  }

  /**
   * Answer with a list of error messages, typically produced by the
   * <code>Message.validate()</code> method
   * @return
   */
  public List<String> getReasons() {
    return reasons;
  }

}
