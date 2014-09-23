package org.nulleins.formats.iso8583;

/**
 * Sample of a business value to include in an ISO message; this class
 * represents a credit card number that should not be transmitted in
 * the clear over the wire
 * @author phillipsr
 */
public final class CardNumber {
  private static final String STARS = "***************";
  private final long number;

  public CardNumber(long number) {
    this.number = number;
  }

  /**
   * Return the card number, obfusticating it if it looks
   * like a valid card number
   */
  @Override
  public String toString() {
    String cardNum = number + "";
    int length = cardNum.length();
    if (length <= 4) {
      return cardNum; // not a valid card number, don't bother obfusticating
    }
    return cardNum.substring(0, 4) + STARS.substring(0, length - 6) + cardNum.substring(length - 2);
  }
}
