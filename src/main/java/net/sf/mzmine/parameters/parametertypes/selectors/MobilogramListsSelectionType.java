package net.sf.mzmine.parameters.parametertypes.selectors;

public enum MobilogramListsSelectionType {

  GUI_SELECTED_PEAKLISTS("As selected in main window"), //
  ALL_PEAKLISTS("All peak lists"), //
  SPECIFIC_PEAKLISTS("Specific peak lists"), //
  NAME_PATTERN("Mobilogram list name pattern"), //
  BATCH_LAST_PEAKLISTS("Those created by previous batch step");

  private final String stringValue;

  MobilogramListsSelectionType(String stringValue) {
    this.stringValue = stringValue;
  }

  @Override
  public String toString() {
    return stringValue;
  }

}
