package net.sf.mzmine.parameters.parametertypes.selectors;

public enum MobilogramListsSelectionType {

  GUI_SELECTED_MOBILOGRAMLISTS("As selected in main window"), //
  ALL_MOBILOGRAMLISTS("All peak lists"), //
  SPECIFIC_MOBILOGRAMLISTS("Specific mobilogram lists"), //
  NAME_PATTERN("Mobilogram list name pattern"), //
  BATCH_LAST_MOBILOGRAMLISTS("Those created by previous batch step");

  private final String stringValue;

  MobilogramListsSelectionType(String stringValue) {
    this.stringValue = stringValue;
  }

  @Override
  public String toString() {
    return stringValue;
  }

}
