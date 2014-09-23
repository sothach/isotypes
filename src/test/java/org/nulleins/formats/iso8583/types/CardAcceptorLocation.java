/**
 *
 */
package org.nulleins.formats.iso8583.types;

/**
 * [PH Rumukrushi          Porthar      PHNG]
 * The location information (positions 1 - 23), exclusive of city, state and country
 * The city (positions 24 - 36) in which the Point-of-Service is located
 * The state (positions 37 - 38) in which the Point-of-Service is located
 * The country (positions 39 - 40) in which the Point-of-Service is located
 * @author phillipsr
 */
public class CardAcceptorLocation {
  private String location;
  private String city;
  private String state;
  private String country;

  public CardAcceptorLocation(String location, String city, String state, String country) {
    this.location = location;
    this.city = city;
    this.state = state;
    this.country = country;
  }

  @Override
  public String toString() {
    return "loc=[" + location + "] city=[" + city + "] state=[" + state + "] country=[" + country + "]";
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

}
