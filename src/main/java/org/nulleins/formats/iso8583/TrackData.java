package org.nulleins.formats.iso8583;


/**
 * Representation of the Track Data stored on a financial transaction card's magnetic strips (see ISO7813)
 * <p/>
 * @author phillipsr
 */
public class TrackData {
  public enum Track {TRACK1, TRACK2, TRACK3}

  private final Track type;
  private long primaryAccountNumber; // PAN : Primary Account Number, up to 19 digits, as defined in ISO/IEC 7812-1
  private String[] name;
  private int expirationDate;        // ED : Expiration date, YYMM
  private int serviceCode;           // SC : Service code, 3 digits
  private String discretionaryData;  // DD : Discretionary data, balance of available digits

  /**
   * @param type
   */
  public TrackData(final Track type) {
    this.type = type;
  }

  public long getPrimaryAccountNumber() {
    return primaryAccountNumber;
  }

  public void setPrimaryAccountNumber(final long primaryAccountNumber) {
    this.primaryAccountNumber = primaryAccountNumber;
  }

  public int getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(final int expirationDate) {
    this.expirationDate = expirationDate;
  }

  public int getServiceCode() {
    return serviceCode;
  }

  public void setServiceCode(final int serviceCode) {
    this.serviceCode = serviceCode;
  }

  public String getDiscretionaryData() {
    return discretionaryData;
  }

  public void setDiscretionaryData(final String string) {
    this.discretionaryData = string;
  }

  /**
   * Answer with the name field as an array of elements
   * @return name {Surname, First Name or Initial, Middle Name or Initial, Title}
   * @throws IllegalStateException if this method is called on Track2 or Track3 data objects
   */
  public String[] getName() {
    if (type != Track.TRACK1) {
      throw new IllegalStateException("No name field available for " + type.toString());
    }
    return name;
  }

  /**
   * Set the name field from an array of elements
   * @param name array of {Surname, First Name or Initial, Middle Name or Initial, Title}
   * @throws IllegalStateException    if this method is called on Track2 or Track3 data objects
   * @throws IllegalArgumentException if name is null or not an array of four elements
   */
  public void setName(final String[] name) {
    if (type != Track.TRACK1) {
      throw new IllegalStateException("Cannot set name field for " + type.toString());
    }
    if (name == null || name.length != 4) {
      throw new IllegalArgumentException("name must be an array of four elements");
    }
    this.name = name;
  }

  public Track getType() {
    return type;
  }

  /**
   * Answer with the canonical string representation of the track data, including field separators
   * appropriate for the variant; does not include start- and end-sentinel characters, nor the
   * calculated LRC value
   */
  @Override
  public String toString() {
    return
        (type == Track.TRACK1 ? "B" : "") +
            primaryAccountNumber + (type == Track.TRACK1 ? ("^" + formatName()) : "") +
            (type == Track.TRACK1 ? "^" : "=") +
            expirationDate + serviceCode +
            (discretionaryData != null ? discretionaryData : "");
  }

  /**
   * Answer with a string representing the name field according to the ISO7813 standard
   * @return Canonical string representation of the track data fields
   * @throws IllegalStateException if this method is called on Track2 or Track3 data objects
   */
  public String formatName() {
    if (type != Track.TRACK1) {
      throw new IllegalStateException("No name field set for " + type.toString());
    }
    if (name == null) {
      return "";
    }
    return name[0] + "/" + name[1] + (!name[2].isEmpty() ? (" " + name[2]) : "") + "." + name[3];
  }

}
