package org.seefin.formats.iso8583;

import java.math.BigInteger;

public class PaymentRequestBean {
  public CardNumber getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(CardNumber cardNumber) {
    this.cardNumber = cardNumber;
  }

  public BigInteger getAmount() {
    return amount;
  }

  public void setAmount(BigInteger amount) {
    this.amount = amount;
  }

  public int getAcquierID() {
    return acquierID;
  }

  public void setAcquierID(int acquierID) {
    this.acquierID = acquierID;
  }

  public long getExtReference() {
    return rrn;
  }

  public void setExtReference(long rrn) {
    this.rrn = rrn;
  }

  public String getCardTermId() {
    return cardTermId;
  }

  public void setCardTermId(String cardTermId) {
    this.cardTermId = cardTermId;
  }

  public String getCardTermName() {
    return cardTermName;
  }

  public void setCardTermName(String cardTermName) {
    this.cardTermName = cardTermName;
  }

  public long getMsisdn() {
    return msisdn;
  }

  public void setMsisdn(long msisdn) {
    this.msisdn = msisdn;
  }

  public int getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(int currencyCode) {
    this.currencyCode = currencyCode;
  }

  private CardNumber cardNumber;
  private BigInteger amount;
  private int acquierID;
  private long rrn;
  private String cardTermId;
  private String cardTermName;
  private long msisdn;
  private int currencyCode;
  private int originalData;

  public int getOriginalData() {
    return originalData;
  }

  /**
   * @param i
   */
  public void setOriginalData(int data) {
    this.originalData = data;
  }
}