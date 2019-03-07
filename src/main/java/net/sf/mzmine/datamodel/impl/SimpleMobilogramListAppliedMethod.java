package net.sf.mzmine.datamodel.impl;

import javax.annotation.Nonnull;
import net.sf.mzmine.datamodel.MobilogramList.MobilogramListAppliedMethod;
import net.sf.mzmine.parameters.ParameterSet;

public class SimpleMobilogramListAppliedMethod implements MobilogramListAppliedMethod {

  private String description;
  private String parameters;

  public SimpleMobilogramListAppliedMethod(String description, ParameterSet parameters) {
    this.description = description;
    if (parameters != null) {
      this.parameters = parameters.toString();
    } else {
      this.parameters = "";
    }
  }

  public SimpleMobilogramListAppliedMethod(String description, String parameters) {
    this.description = description;
    this.parameters = parameters;
  }

  public SimpleMobilogramListAppliedMethod(String description) {
    this.description = description;
  }

  @Override
  public @Nonnull String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return description;
  }

  @Override
  public @Nonnull String getParameters() {
    return parameters;
  }

}
