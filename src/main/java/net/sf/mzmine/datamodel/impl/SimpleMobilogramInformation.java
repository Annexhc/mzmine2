package net.sf.mzmine.datamodel.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.sf.mzmine.datamodel.MobilogramInformation;

public class SimpleMobilogramInformation implements MobilogramInformation {

  private final Map<String, String> properties;

  // ------------------------------------------------------------------------
  // ----- Constructors -----------------------------------------------------
  // ------------------------------------------------------------------------

  public SimpleMobilogramInformation() {
    properties = new HashMap<>();
  }

  public SimpleMobilogramInformation(String propertyName, String propertyValue) {
    this();
    properties.put(propertyName, propertyValue);
  }

  public SimpleMobilogramInformation(Map<String, String> properties) {
    this.properties = properties;
  }

  // ------------------------------------------------------------------------
  // ----- Methods ----------------------------------------------------------
  // ------------------------------------------------------------------------

  public void addProperty(String name, String value) {
    properties.put(name, value);
  }

  public void addProperty(Map<String, String> properties) {
    this.properties.putAll(properties);
  }

  // ------------------------------------------------------------------------
  // ----- Properties -------------------------------------------------------
  // ------------------------------------------------------------------------

  @Override
  @Nonnull
  public String getPropertyValue(String propertyName) {
    return properties.get(propertyName);
  }

  @Override
  @Nonnull
  public String getPropertyValue(String propertyName, String defaultValue) {
    String value = properties.get(propertyName);
    if (value == null)
      value = defaultValue;
    return value;
  }

  @Override
  @Nonnull
  public Map<String, String> getAllProperties() {
    return properties;
  }

  @Override
  @Nonnull
  public synchronized SimpleMobilogramInformation clone() {
    return new SimpleMobilogramInformation(new HashMap<>(properties));
  }
}
