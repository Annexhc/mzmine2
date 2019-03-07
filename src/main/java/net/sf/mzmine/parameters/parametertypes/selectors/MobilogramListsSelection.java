package net.sf.mzmine.parameters.parametertypes.selectors;

import java.util.ArrayList;
import com.google.common.base.Strings;
import net.sf.mzmine.datamodel.MobilogramList;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.util.TextUtils;

public class MobilogramListsSelection implements Cloneable {

  private MobilogramListsSelectionType selectionType =
      MobilogramListsSelectionType.GUI_SELECTED_MOBILOGRAMLISTS;
  private MobilogramList specificMobilogramLists[];
  private String namePattern;
  private MobilogramList batchLastMobilogramLists[];

  public MobilogramList[] getMatchingMobilogramLists() {

    switch (selectionType) {

      case GUI_SELECTED_MOBILOGRAMLISTS:
        return MZmineCore.getDesktop().getSelectedMobilogramLists();
      case ALL_MOBILOGRAMLISTS:
        return MZmineCore.getProjectManager().getCurrentProject().getMobilogramLists();
      case SPECIFIC_MOBILOGRAMLISTS:
        if (specificMobilogramLists == null)
          return new MobilogramList[0];
        return specificMobilogramLists;
      case NAME_PATTERN:
        if (Strings.isNullOrEmpty(namePattern))
          return new MobilogramList[0];
        ArrayList<MobilogramList> matchingMobilogramLists = new ArrayList<MobilogramList>();
        MobilogramList allMobilogramLists[] =
            MZmineCore.getProjectManager().getCurrentProject().getMobilogramLists();

        plCheck: for (MobilogramList pl : allMobilogramLists) {

          final String plName = pl.getName();

          final String regex = TextUtils.createRegexFromWildcards(namePattern);

          if (plName.matches(regex)) {
            if (matchingMobilogramLists.contains(pl))
              continue;
            matchingMobilogramLists.add(pl);
            continue plCheck;
          }
        }
        return matchingMobilogramLists.toArray(new MobilogramList[0]);
      case BATCH_LAST_MOBILOGRAMLISTS:
        if (batchLastMobilogramLists == null)
          return new MobilogramList[0];
        return batchLastMobilogramLists;
    }

    throw new IllegalStateException("This code should be unreachable");

  }

  public MobilogramListsSelectionType getSelectionType() {
    return selectionType;
  }

  public void setSelectionType(MobilogramListsSelectionType selectionType) {
    this.selectionType = selectionType;
  }

  public MobilogramList[] getSpecificMobilogramLists() {
    return specificMobilogramLists;
  }

  public void setSpecificMobilogramLists(MobilogramList[] specificMobilogramLists) {
    this.specificMobilogramLists = specificMobilogramLists;
  }

  public String getNamePattern() {
    return namePattern;
  }

  public void setNamePattern(String namePattern) {
    this.namePattern = namePattern;
  }

  public void setBatchLastMobilogramLists(MobilogramList[] batchLastMobilogramLists) {
    this.batchLastMobilogramLists = batchLastMobilogramLists;
  }

  public MobilogramListsSelection clone() {
    MobilogramListsSelection newSelection = new MobilogramListsSelection();
    newSelection.selectionType = selectionType;
    newSelection.specificMobilogramLists = specificMobilogramLists;
    newSelection.namePattern = namePattern;
    return newSelection;
  }

  public String toString() {
    StringBuilder str = new StringBuilder();
    MobilogramList pls[] = getMatchingMobilogramLists();
    for (int i = 0; i < pls.length; i++) {
      if (i > 0)
        str.append("\n");
      str.append(pls[i].getName());
    }
    return str.toString();
  }
}
