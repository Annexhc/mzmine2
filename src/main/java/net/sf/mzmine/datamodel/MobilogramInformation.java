package net.sf.mzmine.datamodel;

import java.util.Map;
import javax.annotation.Nonnull;

public interface MobilogramInformation {
  /**
   * Returns the value of a property
   * 
   * @param property name
   * @return
   */

  @Nonnull
  String getPropertyValue(String property);

  @Nonnull
  String getPropertyValue(String property, String defaultValue);

  /**
   * Returns all the properties in the form of a map <key, value>
   * 
   * @return
   */

  @Nonnull
  Map<String, String> getAllProperties();

  /**
   * Returns a copy of PeakInformation object
   * 
   * @return
   */

  @Nonnull
  public Object clone();
}
