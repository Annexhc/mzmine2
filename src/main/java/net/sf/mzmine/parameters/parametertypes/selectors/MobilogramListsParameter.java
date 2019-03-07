package net.sf.mzmine.parameters.parametertypes.selectors;

import java.util.ArrayList;
import java.util.Collection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.google.common.base.Strings;
import net.sf.mzmine.datamodel.MobilogramList;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.parameters.UserParameter;

public class MobilogramListsParameter
    implements UserParameter<MobilogramListsSelection, MobilogramListsComponent> {

  private String name = "Mobilogram lists";
  private int minCount, maxCount;

  private MobilogramListsSelection value;

  public MobilogramListsParameter() {
    this(1, Integer.MAX_VALUE);
  }

  public MobilogramListsParameter(int minCount) {
    this(minCount, Integer.MAX_VALUE);
  }

  public MobilogramListsParameter(int minCount, int maxCount) {
    this.minCount = minCount;
    this.maxCount = maxCount;
  }

  public MobilogramListsParameter(String name, int minCount, int maxCount) {
    this.name = name;
    this.minCount = minCount;
    this.maxCount = maxCount;
  }

  @Override
  public MobilogramListsSelection getValue() {
    return value;
  }

  @Override
  public void setValue(MobilogramListsSelection newValue) {
    this.value = newValue;
  }

  public void setValue(MobilogramListsSelectionType selectionType, MobilogramList peakLists[]) {
    if (value == null)
      value = new MobilogramListsSelection();
    value.setSelectionType(selectionType);
    value.setSpecificMobilogramLists(peakLists);
  }

  public void setValue(MobilogramListsSelectionType selectionType) {
    if (value == null)
      value = new MobilogramListsSelection();
    value.setSelectionType(selectionType);
  }

  @Override
  public MobilogramListsParameter cloneParameter() {
    MobilogramListsParameter copy = new MobilogramListsParameter(name, minCount, maxCount);
    if (value != null)
      copy.value = value.clone();
    return copy;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return "Mobilogram lists that this module will take as its input.";
  }

  @Override
  public boolean checkValue(Collection<String> errorMessages) {
    MobilogramList matchingMobilogramLists[];
    if (value == null)
      matchingMobilogramLists = new MobilogramList[0];
    else
      matchingMobilogramLists = value.getMatchingMobilogramLists();

    if (matchingMobilogramLists.length < minCount) {
      errorMessages.add("At least " + minCount + " peak lists  must be selected");
      return false;
    }
    if (matchingMobilogramLists.length > maxCount) {
      errorMessages.add("Maximum " + maxCount + " peak lists may be selected");
      return false;
    }
    return true;
  }

  @Override
  public void loadValueFromXML(Element xmlElement) {

    MobilogramList[] currentDataMobilogramLists =
        MZmineCore.getProjectManager().getCurrentProject().getMobilogramLists();

    MobilogramListsSelectionType selectionType;
    final String attrValue = xmlElement.getAttribute("type");

    if (Strings.isNullOrEmpty(attrValue))
      selectionType = MobilogramListsSelectionType.GUI_SELECTED_MOBILOGRAMLISTS;
    else
      selectionType = MobilogramListsSelectionType.valueOf(xmlElement.getAttribute("type"));

    ArrayList<Object> newValues = new ArrayList<Object>();

    NodeList items = xmlElement.getElementsByTagName("specific_peak_list");
    for (int i = 0; i < items.getLength(); i++) {
      String itemString = items.item(i).getTextContent();
      for (MobilogramList df : currentDataMobilogramLists) {
        if (df.getName().equals(itemString))
          newValues.add(df);
      }
    }
    MobilogramList specificMobilogramLists[] = newValues.toArray(new MobilogramList[0]);

    String namePattern = null;
    items = xmlElement.getElementsByTagName("name_pattern");
    for (int i = 0; i < items.getLength(); i++) {
      namePattern = items.item(i).getTextContent();
    }

    this.value = new MobilogramListsSelection();
    this.value.setSelectionType(selectionType);
    this.value.setSpecificMobilogramLists(specificMobilogramLists);
    this.value.setNamePattern(namePattern);
  }

  @Override
  public void saveValueToXML(Element xmlElement) {
    if (value == null)
      return;
    Document parentDocument = xmlElement.getOwnerDocument();
    xmlElement.setAttribute("type", value.getSelectionType().name());

    if (value.getSpecificMobilogramLists() != null) {
      for (MobilogramList item : value.getSpecificMobilogramLists()) {
        Element newElement = parentDocument.createElement("specific_peak_list");
        newElement.setTextContent(item.getName());
        xmlElement.appendChild(newElement);
      }
    }

    if (value.getNamePattern() != null) {
      Element newElement = parentDocument.createElement("name_pattern");
      newElement.setTextContent(value.getNamePattern());
      xmlElement.appendChild(newElement);
    }

  }

  @Override
  public MobilogramListsComponent createEditingComponent() {
    return new MobilogramListsComponent();
  }

  @Override
  public void setValueFromComponent(MobilogramListsComponent component) {
    value = component.getValue();
  }

  @Override
  public void setValueToComponent(MobilogramListsComponent component,
      MobilogramListsSelection newValue) {
    component.setValue(newValue);
  }

}
